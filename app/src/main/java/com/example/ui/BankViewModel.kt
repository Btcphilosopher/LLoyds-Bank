package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.GeminiApiService
import com.example.data.api.GeminiContent
import com.example.data.api.GeminiPart
import com.example.data.api.GeminiRequest
import com.example.data.api.RetrofitClient
import com.example.data.local.*
import com.example.data.repository.BankRepository
import com.example.data.repository.PaymentResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BankViewModel(application: Application) : AndroidViewModel(application) {

    private val db = BankDatabase.getDatabase(application)
    private val repository = BankRepository(db.bankDao())

    // UI state flows
    val user = repository.userFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val accounts = repository.accountsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val transactions = repository.transactionsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val beneficiaries = repository.beneficiariesFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val savingsGoals = repository.savingsGoalsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val fraudLogs = repository.fraudLogsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation and UX state
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _activeTab = MutableStateFlow(0) // 0: Home, 1: Payments, 2: Cards, 3: Savings, 4: Security, 5: Specs
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _selectedAccount = MutableStateFlow<AccountEntity?>(null)
    val selectedAccount: StateFlow<AccountEntity?> = _selectedAccount.asStateFlow()

    // Login state
    private val _loginPin = MutableStateFlow("")
    val loginPin: StateFlow<String> = _loginPin.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Support Chat
    private val _supportChatHistory = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("Hello! I am the Lloyds secure virtual assistant, powered by Gemini. How can I assist you with your accounts, UK Faster Payments, or security concerns today?", false)
        )
    )
    val supportChatHistory: StateFlow<List<ChatMessage>> = _supportChatHistory.asStateFlow()

    private val _isWaitingForSupport = MutableStateFlow(false)
    val isWaitingForSupport: StateFlow<Boolean> = _isWaitingForSupport.asStateFlow()

    // AI Insight state
    private val _aiInsight = MutableStateFlow<String?>(null)
    val aiInsight: StateFlow<String?> = _aiInsight.asStateFlow()

    private val _isGeneratingInsight = MutableStateFlow(false)
    val isGeneratingInsight: StateFlow<Boolean> = _isGeneratingInsight.asStateFlow()

    // Card PIN reveal
    private val _cardPinRevealed = MutableStateFlow(false)
    val cardPinRevealed: StateFlow<Boolean> = _cardPinRevealed.asStateFlow()

    // Payment state outputs
    private val _paymentState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentState: StateFlow<PaymentUiState> = _paymentState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulateData()
            // Set first account as default selected
            accounts.filter { it.isNotEmpty() }.collect {
                if (_selectedAccount.value == null) {
                    _selectedAccount.value = it.firstOrNull()
                }
            }
        }
    }

    fun login(pin: String) {
        if (pin == "1234") {
            _isLoggedIn.value = true
            _loginError.value = null
        } else {
            _loginError.value = "Invalid security PIN. Please try again (Hint: 1234)."
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _loginPin.value = ""
    }

    fun selectTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun selectAccount(account: AccountEntity) {
        _selectedAccount.value = account
    }

    fun appendPinChar(char: Char) {
        if (_loginPin.value.length < 4) {
            _loginPin.value += char
            _loginError.value = null
            if (_loginPin.value.length == 4) {
                login(_loginPin.value)
            }
        }
    }

    fun clearPin() {
        _loginPin.value = ""
        _loginError.value = null
    }

    fun toggleCardFreeze(accountId: String) {
        viewModelScope.launch {
            repository.toggleAccountFreeze(accountId)
            // Refresh currently selected account state
            _selectedAccount.value = accounts.value.find { it.id == accountId }
        }
    }

    fun revealCardPin(reveal: Boolean) {
        _cardPinRevealed.value = reveal
    }

    fun executePayment(
        sourceAccountId: String,
        beneficiaryName: String,
        accountNumber: String,
        sortCode: String,
        amount: Double,
        reference: String
    ) {
        viewModelScope.launch {
            _paymentState.value = PaymentUiState.Loading
            val result = repository.executePayment(
                sourceAccountId = sourceAccountId,
                beneficiaryName = beneficiaryName,
                accountNumber = accountNumber,
                sortCode = sortCode,
                amount = amount,
                reference = reference
            )
            when (result) {
                is PaymentResult.Success -> {
                    _paymentState.value = PaymentUiState.Success(result.message, result.roundUpMessage)
                }
                is PaymentResult.FraudBlock -> {
                    _paymentState.value = PaymentUiState.FraudBlocked(result.reason)
                }
                is PaymentResult.Error -> {
                    _paymentState.value = PaymentUiState.Error(result.error)
                }
            }
        }
    }

    fun clearPaymentState() {
        _paymentState.value = PaymentUiState.Idle
    }

    fun createSavingsGoal(name: String, target: Double, isRoundUp: Boolean) {
        viewModelScope.launch {
            repository.saveSavingsGoal(name, target, isRoundUp)
        }
    }

    fun addBeneficiary(name: String, accountNumber: String, sortCode: String, bankName: String) {
        viewModelScope.launch {
            repository.saveBeneficiary(name, accountNumber, sortCode, bankName)
        }
    }

    // --- Gemini AI Features ---

    // 1. Live support chatbot using Gemini API with high-fidelity backup
    fun askSupportQuestion(question: String) {
        if (question.isBlank()) return

        // Add user message to chat list
        val currentChat = _supportChatHistory.value.toMutableList()
        currentChat.add(ChatMessage(question, true))
        _supportChatHistory.value = currentChat
        _isWaitingForSupport.value = true

        viewModelScope.launch {
            val responseText = callGeminiApi(
                prompt = "You are the Lloyds Bank Secure AI Assistant. Answer this customer's banking question professionally, keeping security, British banking context, and customer reassurance in mind. Here is the customer question: '$question'."
            )
            
            val updatedChat = _supportChatHistory.value.toMutableList()
            updatedChat.add(ChatMessage(responseText, false))
            _supportChatHistory.value = updatedChat
            _isWaitingForSupport.value = false
        }
    }

    // 2. Automated AI financial insights based on the user's active database state
    fun generateAiSpendingInsights() {
        _isGeneratingInsight.value = true
        _aiInsight.value = null

        viewModelScope.launch {
            val txList = transactions.value.take(10)
            val accountsList = accounts.value

            val prompt = StringBuilder()
            prompt.append("You are a financial planning and wealth coach for Lloyds Banking Group. Review the customer's financial state below and provide 3-4 highly concise, actionable financial health suggestions or insights regarding spending, savings, and security.\n\n")
            prompt.append("Current Accounts:\n")
            accountsList.forEach {
                prompt.append("- ${it.name}: £${it.balance} (Sort Code: ${it.sortCode})\n")
            }
            prompt.append("\nRecent Transactions:\n")
            txList.forEach {
                prompt.append("- ${it.title} (${it.category}): £${it.amount} [${it.type}]\n")
            }

            val response = callGeminiApi(prompt.toString())
            _aiInsight.value = response
            _isGeneratingInsight.value = false
        }
    }

    private suspend fun callGeminiApi(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val isDefaultKey = apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY"

        if (isDefaultKey) {
            // Provide gorgeous simulated AI feedback explaining how to connect Gemini key
            return@withContext getSimulatedResponse(prompt)
        }

        try {
            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        parts = listOf(GeminiPart(text = prompt))
                    )
                )
            )
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "I was able to connect to Gemini but received empty suggestions. Please try again."
        } catch (e: Exception) {
            // Graceful fallback to rich local rule-based simulation on network failure
            getSimulatedResponse(prompt, e.message)
        }
    }

    private fun getSimulatedResponse(prompt: String, errorMsg: String? = null): String {
        val lowerPrompt = prompt.lowercase()
        val suffix = "\n\n*(Note: Running in high-fidelity sandbox mode. Configure your GEMINI_API_KEY in the AI Studio Secrets panel to enable real live Gemini AI calls!)*"

        return when {
            lowerPrompt.contains("financial health") || lowerPrompt.contains("wealth coach") -> {
                """
**Lloyds Smart Financial Coach Insights:**

• **Club Lloyds Interest Maximizer:** Your Current Account holds £5,432.89. To maximize yield, consider moving any funds above a £2,000 baseline into your **Lloyds Easy Saver** which yields 4.25% interest. This will generate an extra £145 annually in passive compound gains.

• **Grocery Budget Check:** Sainsbury's transactions represent a steady portion of recent debits. Consolidating shopping into bulk trips can reduce peripheral impulse buys.

• **Automated Round-ups:** Your Round-up savings setting on 'New Hybrid SUV' is actively routing change. This month, you saved an extra £22.40 without any manual input! Keep this automation running.

• **Security Advisory:** You have Biometrics and MFA active. This puts your security index in our top 1% tier of digital safety. Never share passcodes with anyone.
                """.trimIndent() + suffix
            }
            lowerPrompt.contains("transfer") || lowerPrompt.contains("payment") || lowerPrompt.contains("bacs") -> {
                "Lloyds supports UK Faster Payments which are processed within 2 minutes. For scheduled or standing orders, BACS-style transfers are processed within 3 working days. Your transaction risk is scanned instantly by our Sentinel engine to block unauthorized crypto/scam transfers." + suffix
            }
            lowerPrompt.contains("freeze") || lowerPrompt.contains("card") || lowerPrompt.contains("pin") -> {
                "You can freeze/unfreeze your Visa debit or credit cards instantly in the Cards tab. Freezing prevents contactless taps, ATM cash withdrawals, and online purchases. You can also securely reveal your card's 4-digit PIN in-app; never write your PIN down." + suffix
            }
            lowerPrompt.contains("fraud") || lowerPrompt.contains("suspicious") || lowerPrompt.contains("security") -> {
                "Our Sentinel AI Fraud engine operates 24/7. Transactions above £5,000, transfers to high-risk keywords (like 'crypto exchange'), or rapid geographic anomalies will trigger an instant security freeze to protect your life savings." + suffix
            }
            else -> {
                "Thank you for contacting Lloyds Secure Support. I can help you with transactions, savings goals, card management, or security. Our automated systems are fully compliant with GDPR and PCI-DSS standards. How else can I assist you today?" + suffix
            }
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDatabase()
            _activeTab.value = 0
            _selectedAccount.value = accounts.value.firstOrNull()
            _aiInsight.value = null
        }
    }
}

data class ChatMessage(val text: String, val isUser: Boolean)

sealed class PaymentUiState {
    object Idle : PaymentUiState()
    object Loading : PaymentUiState()
    data class Success(val message: String, val roundUpMessage: String) : PaymentUiState()
    data class FraudBlocked(val reason: String) : PaymentUiState()
    data class Error(val error: String) : PaymentUiState()
}

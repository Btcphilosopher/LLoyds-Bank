package com.example.data.repository

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class BankRepository(private val bankDao: BankDao) {

    // Reactive streams for the UI
    val userFlow: Flow<UserEntity?> = bankDao.getUser()
    val accountsFlow: Flow<List<AccountEntity>> = bankDao.getAllAccounts()
    val transactionsFlow: Flow<List<TransactionEntity>> = bankDao.getAllTransactions()
    val beneficiariesFlow: Flow<List<BeneficiaryEntity>> = bankDao.getAllBeneficiaries()
    val savingsGoalsFlow: Flow<List<SavingsGoalEntity>> = bankDao.getAllSavingsGoals()
    val fraudLogsFlow: Flow<List<FraudLogEntity>> = bankDao.getAllFraudLogs()

    // Initialize mock data if DB is empty
    suspend fun checkAndPrepopulateData() {
        val currentUser = bankDao.getUser().firstOrNull()
        if (currentUser == null) {
            // Create default user
            val defaultUser = UserEntity(
                id = "lloyds_user_001",
                name = "Dr. Thomas Harrison",
                email = "thomas.harrison@lloyds-customer.co.uk",
                isBiometricsEnabled = true,
                isMfaSetup = true,
                pinHash = "1234"
            )
            bankDao.insertUser(defaultUser)

            // Create default bank accounts (Lloyds formats)
            val accounts = listOf(
                AccountEntity(
                    id = "acc_current",
                    name = "Club Lloyds Current Account",
                    type = "CURRENT",
                    balance = 5432.89,
                    availableFunds = 5382.89,
                    accountNumber = "77341298",
                    sortCode = "30-90-15" // Classic Lloyds Sort Code
                ),
                AccountEntity(
                    id = "acc_savings",
                    name = "Lloyds Easy Saver (0.85% AER)",
                    type = "SAVINGS",
                    balance = 12450.50,
                    availableFunds = 12450.50,
                    accountNumber = "88231149",
                    sortCode = "30-90-15",
                    interestRate = 4.25
                ),
                AccountEntity(
                    id = "acc_joint",
                    name = "Joint House Account",
                    type = "JOINT",
                    balance = 824.12,
                    availableFunds = 824.12,
                    accountNumber = "22340056",
                    sortCode = "30-94-30"
                )
            )
            bankDao.insertAccounts(accounts)

            // Create default saved beneficiaries (Payees)
            val payees = listOf(
                BeneficiaryEntity("payee_1", "Sarah Harrison", "11029348", "30-90-15", "Lloyds Bank"),
                BeneficiaryEntity("payee_2", "OVO Energy UK", "99332211", "60-11-23", "NatWest"),
                BeneficiaryEntity("payee_3", "HM Revenue & Customs", "22446688", "08-30-00", "Barclays"),
                BeneficiaryEntity("payee_4", "Tesco Petrol UK", "55443322", "20-00-00", "HSBC")
            )
            for (p in payees) {
                bankDao.insertBeneficiary(p)
            }

            // Create default savings goals
            val goals = listOf(
                SavingsGoalEntity("goal_1", "New Hybrid SUV", 30000.0, 12000.0, true),
                SavingsGoalEntity("goal_2", "Scottish Highlands Holiday", 1500.0, 450.0, false),
                SavingsGoalEntity("goal_3", "Emergency Fund", 5000.0, 2500.0, false)
            )
            for (g in goals) {
                bankDao.insertSavingsGoal(g)
            }

            // Create default transactions
            val calendar = Calendar.getInstance()
            val transactions = listOf(
                TransactionEntity(
                    accountId = "acc_current",
                    title = "Salary Deposit",
                    description = "LLOYDS GROUP PAYROLL",
                    category = "Salary",
                    amount = 3250.00,
                    timestamp = calendar.timeInMillis - 2 * 3600 * 1000, // 2 hrs ago
                    type = "CREDIT",
                    status = "COMPLETED"
                ),
                TransactionEntity(
                    accountId = "acc_current",
                    title = "Sainsbury's Supermarket",
                    description = "SAINSBURYS SML LONDON UK",
                    category = "Groceries",
                    amount = 64.20,
                    timestamp = calendar.timeInMillis - 5 * 3600 * 1000,
                    type = "DEBIT",
                    status = "COMPLETED"
                ),
                TransactionEntity(
                    accountId = "acc_current",
                    title = "OVO Energy Bill",
                    description = "OVO ENERGY DIR-DEB",
                    category = "Bills",
                    amount = 145.00,
                    timestamp = calendar.timeInMillis - 24 * 3600 * 1000, // 1 day ago
                    type = "DEBIT",
                    status = "COMPLETED"
                ),
                TransactionEntity(
                    accountId = "acc_current",
                    title = "Amazon UK Prime",
                    description = "AMZN.CO.UK AMZN DIGITAL",
                    category = "Shopping",
                    amount = 8.99,
                    timestamp = calendar.timeInMillis - 48 * 3600 * 1000, // 2 days ago
                    type = "DEBIT",
                    status = "COMPLETED"
                ),
                TransactionEntity(
                    accountId = "acc_current",
                    title = "Transfer to Savings",
                    description = "Transfer to Lloyds Easy Saver",
                    category = "Transfer",
                    amount = 500.00,
                    timestamp = calendar.timeInMillis - 3 * 24 * 3600 * 1000,
                    type = "DEBIT",
                    status = "COMPLETED"
                ),
                TransactionEntity(
                    accountId = "acc_savings",
                    title = "Savings Deposit",
                    description = "Transfer from Club Lloyds",
                    category = "Transfer",
                    amount = 500.00,
                    timestamp = calendar.timeInMillis - 3 * 24 * 3600 * 1000,
                    type = "CREDIT",
                    status = "COMPLETED"
                ),
                TransactionEntity(
                    accountId = "acc_current",
                    title = "Tesco Petrol",
                    description = "TESCO PETROL 4301 LONDON",
                    category = "Travel",
                    amount = 55.40,
                    timestamp = calendar.timeInMillis - 4 * 24 * 3600 * 1000,
                    type = "DEBIT",
                    status = "COMPLETED"
                )
            )
            bankDao.insertTransactions(transactions)

            // Populate some initial fraud signals / compliance audit logs
            val fraudLogs = listOf(
                FraudLogEntity(
                    ruleName = "Device Fingerprint Matched",
                    description = "Successful authentication from primary device: Google Pixel 8 Pro.",
                    severity = "LOW",
                    details = "IP: 194.22.103.11 (London, UK). Device Token: d8a9c2-9e81-4bcf-a52d.",
                    isActionTaken = false
                ),
                FraudLogEntity(
                    ruleName = "UK Open Banking Access Granted",
                    description = "Third-party token sync authorized for Moneybox app.",
                    severity = "LOW",
                    details = "API Version: v3.1. Consent Ref: OB-7749-MBX.",
                    isActionTaken = false
                )
            )
            for (f in fraudLogs) {
                bankDao.insertFraudLog(f)
            }
        }
    }

    // Process a payment transfer
    suspend fun executePayment(
        sourceAccountId: String,
        beneficiaryName: String,
        accountNumber: String,
        sortCode: String,
        amount: Double,
        reference: String,
        scheduledDate: String? = null // Null means instant transfer
    ): PaymentResult {
        // Retrieve account
        val account = bankDao.getAccountById(sourceAccountId) ?: return PaymentResult.Error("Source account not found")

        if (account.isFrozen) {
            return PaymentResult.Error("Source account is frozen")
        }

        if (account.balance < amount) {
            return PaymentResult.Error("Insufficient funds. Available: £${account.availableFunds}")
        }

        // --- Fraud Rules Engine Run ---
        val riskScore = evaluateTransactionRisk(beneficiaryName, accountNumber, sortCode, amount)
        val isFraudulent = riskScore >= 80.0

        if (isFraudulent) {
            // Trigger automatic security responses
            val updatedAccount = account.copy(isFrozen = true)
            bankDao.updateAccount(updatedAccount)

            val fraudLog = FraudLogEntity(
                ruleName = "Suspicious Payment Intercepted",
                description = "UK Faster Payment to $beneficiaryName blocked by automated risk engine.",
                severity = "HIGH",
                details = "Blocked transfer of £$amount to $accountNumber ($sortCode). Reason: Anomaly in beneficiary risk profiling. System initiated Account Freeze.",
                isActionTaken = true
            )
            bankDao.insertFraudLog(fraudLog)

            // Log block transaction
            val blockTx = TransactionEntity(
                accountId = sourceAccountId,
                title = "BLOCKED: $beneficiaryName",
                description = "SUSPICIOUS ACTIVITY INTERCEPTED - £$amount",
                category = "FraudAlert",
                amount = amount,
                timestamp = System.currentTimeMillis(),
                type = "DEBIT",
                status = "FAILED",
                riskScore = riskScore,
                isSuspicious = true
            )
            bankDao.insertTransaction(blockTx)

            return PaymentResult.FraudBlock(riskScore, "This payment was flagged as high risk (Score: ${String.format("%.1f", riskScore)}%) by the Lloyds Sentinel AI fraud monitoring engine. For your security, this transaction has been blocked, and your account has been temporarily frozen. Please contact our Security Center immediately.")
        }

        // Apply transfer
        val isCompleted = scheduledDate == null
        val status = if (isCompleted) "COMPLETED" else "PENDING"
        
        // Update account balances
        val newBalance = account.balance - amount
        val newAvailable = account.availableFunds - amount
        bankDao.updateAccount(account.copy(balance = newBalance, availableFunds = newAvailable))

        // Save transaction
        val transaction = TransactionEntity(
            accountId = sourceAccountId,
            title = beneficiaryName,
            description = if (reference.isEmpty()) "UK Faster Payment" else reference,
            category = "Transfer",
            amount = amount,
            timestamp = System.currentTimeMillis(),
            type = "DEBIT",
            status = status,
            riskScore = riskScore,
            isSuspicious = riskScore >= 50.0
        )
        bankDao.insertTransaction(transaction)

        // --- Savings Goals Round-Up Feature Automation ---
        var roundUpFeedback = ""
        if (isCompleted) {
            val roundedValue = Math.ceil(amount)
            val change = roundedValue - amount
            if (change > 0.0 && change < 1.0) {
                // Find a savings goal with round-up enabled
                val goals = bankDao.getAllSavingsGoals().firstOrNull() ?: emptyList()
                val targetGoal = goals.find { it.isRoundUpEnabled }
                if (targetGoal != null) {
                    // Update goal
                    val updatedGoal = targetGoal.copy(currentAmount = targetGoal.currentAmount + change)
                    bankDao.updateSavingsGoal(updatedGoal)

                    // Deduct from current account
                    val finalBalance = newBalance - change
                    bankDao.updateAccount(account.copy(balance = finalBalance, availableFunds = finalBalance))

                    // Log savings round up transaction
                    bankDao.insertTransaction(
                        TransactionEntity(
                            accountId = sourceAccountId,
                            title = "Round-up to ${targetGoal.name}",
                            description = "Lloyds Automated Round-up Save",
                            category = "Investment",
                            amount = change,
                            timestamp = System.currentTimeMillis() + 100,
                            type = "DEBIT",
                            status = "COMPLETED"
                        )
                    )

                    roundUpFeedback = "Plus £${String.format("%.2f", change)} rounded up directly to your '${targetGoal.name}' goal!"
                }
            }
        }

        // If risk was medium, log safety alert but let it pass
        if (riskScore >= 45.0) {
            bankDao.insertFraudLog(
                FraudLogEntity(
                    ruleName = "Medium Risk Payment Cleared",
                    description = "Payment of £$amount to $beneficiaryName triggered warning.",
                    severity = "MEDIUM",
                    details = "Risk Score: ${String.format("%.1f", riskScore)}%. IP and Location verified, payment processed.",
                    isActionTaken = false
                )
            )
        }

        return PaymentResult.Success(riskScore, "Payment of £${String.format("%.2f", amount)} successfully sent to $beneficiaryName. Faster Payments Ref: LBG-${UUID.randomUUID().toString().take(8).uppercase()}.", roundUpFeedback)
    }

    // Quick risk assessment engine based on Lloyds Security guidelines
    private fun evaluateTransactionRisk(name: String, accNum: String, sortCode: String, amount: Double): Double {
        var score = 5.0 // Base baseline risk

        // High amounts increase risk exponentially
        if (amount > 1000.0) score += 20.0
        if (amount > 5000.0) score += 30.0

        // Suspicious keywords in recipient name or account
        val lowerName = name.lowercase()
        if (lowerName.contains("crypto") || lowerName.contains("binance") || lowerName.contains("coinbase")) {
            score += 45.0
        }
        if (lowerName.contains("alert") || lowerName.contains("support") || lowerName.contains("security")) {
            score += 40.0 // Common scam where victims are asked to pay "safe accounts"
        }

        // Random generator based on name hash for consistency
        val hash = abs(name.hashCode() + accNum.hashCode()) % 100
        score += (hash % 15) // Add slight variance

        return score.coerceAtMost(100.0)
    }

    // Toggle card frozen status
    suspend fun toggleAccountFreeze(accountId: String): Boolean {
        val account = bankDao.getAccountById(accountId) ?: return false
        val newFrozen = !account.isFrozen
        bankDao.updateAccount(account.copy(isFrozen = newFrozen))

        // Log action
        bankDao.insertFraudLog(
            FraudLogEntity(
                ruleName = if (newFrozen) "Card Blocked" else "Card Unblocked",
                description = "Account/Card for ${account.name} toggled by customer in-app.",
                severity = "LOW",
                details = "Status changed to ${if (newFrozen) "FROZEN" else "ACTIVE"}.",
                isActionTaken = true
            )
        )
        return newFrozen
    }

    // Save savings goal
    suspend fun saveSavingsGoal(name: String, target: Double, isRoundUp: Boolean) {
        val id = "goal_${UUID.randomUUID().toString().take(6)}"
        if (isRoundUp) {
            // Disable round-up on other goals first to keep only one active
            val goals = bankDao.getAllSavingsGoals().firstOrNull() ?: emptyList()
            for (g in goals) {
                if (g.isRoundUpEnabled) {
                    bankDao.updateSavingsGoal(g.copy(isRoundUpEnabled = false))
                }
            }
        }
        bankDao.insertSavingsGoal(SavingsGoalEntity(id, name, target, 0.0, isRoundUp))
    }

    // Save beneficiary (Payee)
    suspend fun saveBeneficiary(name: String, accNum: String, sortCode: String, bankName: String) {
        val id = "payee_${UUID.randomUUID().toString().take(6)}"
        bankDao.insertBeneficiary(BeneficiaryEntity(id, name, accNum, sortCode, bankName))
    }

    // Reset database to initial sample dataset state
    suspend fun resetDatabase() {
        // Room clean and repopulate
        // Simply update account balances back to original and clear recent transactions
        val defaultUser = UserEntity(
            id = "lloyds_user_001",
            name = "Dr. Thomas Harrison",
            email = "thomas.harrison@lloyds-customer.co.uk",
            isBiometricsEnabled = true,
            isMfaSetup = true,
            pinHash = "1234"
        )
        bankDao.insertUser(defaultUser)

        val accounts = listOf(
            AccountEntity(
                id = "acc_current",
                name = "Club Lloyds Current Account",
                type = "CURRENT",
                balance = 5432.89,
                availableFunds = 5382.89,
                accountNumber = "77341298",
                sortCode = "30-90-15"
            ),
            AccountEntity(
                id = "acc_savings",
                name = "Lloyds Easy Saver (0.85% AER)",
                type = "SAVINGS",
                balance = 12450.50,
                availableFunds = 12450.50,
                accountNumber = "88231149",
                sortCode = "30-90-15",
                interestRate = 4.25
            ),
            AccountEntity(
                id = "acc_joint",
                name = "Joint House Account",
                type = "JOINT",
                balance = 824.12,
                availableFunds = 824.12,
                accountNumber = "22340056",
                sortCode = "30-94-30"
            )
        )
        bankDao.insertAccounts(accounts)
    }
}

sealed class PaymentResult {
    data class Success(val riskScore: Double, val message: String, val roundUpMessage: String) : PaymentResult()
    data class FraudBlock(val riskScore: Double, val reason: String) : PaymentResult()
    data class Error(val error: String) : PaymentResult()
}

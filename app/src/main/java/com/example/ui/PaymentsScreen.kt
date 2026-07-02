package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AccountEntity
import com.example.data.local.BeneficiaryEntity
import com.example.ui.theme.*

@Composable
fun PaymentsScreen(
    viewModel: BankViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsState()
    val beneficiaries by viewModel.beneficiaries.collectAsState()
    val paymentState by viewModel.paymentState.collectAsState()

    var showAddPayeeSection by remember { mutableStateOf(false) }

    // Form states
    var selectedSourceAccount by remember { mutableStateOf<AccountEntity?>(null) }
    var selectedBeneficiary by remember { mutableStateOf<BeneficiaryEntity?>(null) }
    var amountInput by remember { mutableStateOf("") }
    var referenceInput by remember { mutableStateOf("") }

    // Add payee state
    var newPayeeName by remember { mutableStateOf("") }
    var newPayeeAccNum by remember { mutableStateOf("") }
    var newPayeeSortCode by remember { mutableStateOf("") }
    var newPayeeBankName by remember { mutableStateOf("") }

    // Dropdown selectors
    var sourceDropdownExpanded by remember { mutableStateOf(false) }
    var beneficiaryDropdownExpanded by remember { mutableStateOf(false) }

    // Init source account if null
    LaunchedEffect(accounts) {
        if (selectedSourceAccount == null && accounts.isNotEmpty()) {
            selectedSourceAccount = accounts.find { it.type == "CURRENT" } ?: accounts.first()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- Header Section ---
        item {
            Column {
                Text(
                    text = "PAYMENTS & TRANSFERS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "UK Faster Payments Gateway",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }

        // --- Core Payment Form ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "NEW TRANSFER",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LloydsGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    // 1. Source account selector dropdown
                    Column {
                        Text("Select Source Account", style = MaterialTheme.typography.bodySmall)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                .clickable { sourceDropdownExpanded = true }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedSourceAccount?.let { "${it.name} (£${it.balance})" } ?: "Select Source Account",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }

                            DropdownMenu(
                                expanded = sourceDropdownExpanded,
                                onDismissRequest = { sourceDropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                accounts.forEach { account ->
                                    DropdownMenuItem(
                                        text = { Text("${account.name} (Balance: £${account.balance})") },
                                        onClick = {
                                            selectedSourceAccount = account
                                            sourceDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 2. Beneficiary (Payee) dropdown selector
                    Column {
                        Text("Select Payee", style = MaterialTheme.typography.bodySmall)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                                .clickable { beneficiaryDropdownExpanded = true }
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedBeneficiary?.let { "${it.name} (${it.bankName})" } ?: "Select Beneficiary",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Expand")
                            }

                            DropdownMenu(
                                expanded = beneficiaryDropdownExpanded,
                                onDismissRequest = { beneficiaryDropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.85f)
                            ) {
                                if (beneficiaries.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("No payees saved yet.") },
                                        onClick = { beneficiaryDropdownExpanded = false }
                                    )
                                } else {
                                    beneficiaries.forEach { payee ->
                                        DropdownMenuItem(
                                            text = { Text("${payee.name} - ${payee.bankName} (${payee.accountNumber})") },
                                            onClick = {
                                                selectedBeneficiary = payee
                                                beneficiaryDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 3. Amount Field
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { amountInput = it },
                        label = { Text("Amount (£)") },
                        leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = LloydsGreen) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transfer_amount_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 4. Reference Field
                    OutlinedTextField(
                        value = referenceInput,
                        onValueChange = { referenceInput = it },
                        label = { Text("Payment Reference") },
                        leadingIcon = { Icon(Icons.Default.StickyNote2, contentDescription = null, tint = LloydsGreen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("transfer_reference_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // 5. Submit Button
                    Button(
                        onClick = {
                            val amount = amountInput.toDoubleOrNull() ?: 0.0
                            val source = selectedSourceAccount
                            val payee = selectedBeneficiary

                            if (source != null && payee != null && amount > 0.0) {
                                viewModel.executePayment(
                                    sourceAccountId = source.id,
                                    beneficiaryName = payee.name,
                                    accountNumber = payee.accountNumber,
                                    sortCode = payee.sortCode,
                                    amount = amount,
                                    reference = referenceInput
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("execute_transfer_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = LloydsGreen),
                        shape = RoundedCornerShape(12.dp),
                        enabled = selectedSourceAccount != null && selectedBeneficiary != null && amountInput.isNotBlank()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = LloydsGold)
                            Text("Authorise Faster Payment", fontWeight = FontWeight.Bold, color = TextLight)
                        }
                    }
                }
            }
        }

        // --- Add Beneficiary Segment Trigger ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddPayeeSection = !showAddPayeeSection }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = LloydsGreen)
                        Text("Add New UK Beneficiary", fontWeight = FontWeight.Bold)
                    }
                    Icon(
                        imageVector = if (showAddPayeeSection) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle"
                    )
                }
            }
        }

        // --- Add Payee Form Content ---
        if (showAddPayeeSection) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "NEW PAYEE DETAILS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = LloydsGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        OutlinedTextField(
                            value = newPayeeName,
                            onValueChange = { newPayeeName = it },
                            label = { Text("Full Name / Business") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = newPayeeSortCode,
                                onValueChange = { newPayeeSortCode = it },
                                label = { Text("Sort Code (XX-XX-XX)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = newPayeeAccNum,
                                onValueChange = { newPayeeAccNum = it },
                                label = { Text("Account Number") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        OutlinedTextField(
                            value = newPayeeBankName,
                            onValueChange = { newPayeeBankName = it },
                            label = { Text("Bank / Building Society") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Button(
                            onClick = {
                                if (newPayeeName.isNotBlank() && newPayeeAccNum.length == 8) {
                                    viewModel.addBeneficiary(
                                        name = newPayeeName,
                                        accountNumber = newPayeeAccNum,
                                        sortCode = newPayeeSortCode,
                                        bankName = newPayeeBankName
                                    )
                                    // Reset fields
                                    newPayeeName = ""
                                    newPayeeAccNum = ""
                                    newPayeeSortCode = ""
                                    newPayeeBankName = ""
                                    showAddPayeeSection = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = LloydsGreen),
                            shape = RoundedCornerShape(12.dp),
                            enabled = newPayeeName.isNotBlank() && newPayeeAccNum.isNotBlank()
                        ) {
                            Text("Save Beneficiary", color = TextLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- Active Payees list ---
        item {
            Text(
                text = "SAVED PAYEES",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        if (beneficiaries.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No saved payees found. Add one above.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            items(beneficiaries) { payee ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedBeneficiary = payee },
                    border = BorderStroke(
                        width = if (selectedBeneficiary?.id == payee.id) 1.5.dp else 1.dp,
                        color = if (selectedBeneficiary?.id == payee.id) LloydsGreen else MaterialTheme.colorScheme.outlineVariant
                    ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = payee.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${payee.bankName} • Account: ${payee.accountNumber} • SC: ${payee.sortCode}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }

                        if (selectedBeneficiary?.id == payee.id) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = LloydsGreen)
                        }
                    }
                }
            }
        }
    }

    // --- Dynamic Payment Result Sheet/Dialog ---
    if (paymentState != PaymentUiState.Idle) {
        AlertDialog(
            onDismissRequest = { viewModel.clearPaymentState() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = when (paymentState) {
                            is PaymentUiState.Success -> Icons.Default.CheckCircle
                            is PaymentUiState.FraudBlocked -> Icons.Default.Shield
                            else -> Icons.Default.Error
                        },
                        contentDescription = null,
                        tint = when (paymentState) {
                            is PaymentUiState.Success -> AlertGreen
                            is PaymentUiState.FraudBlocked -> AlertRed
                            else -> AlertRed
                        },
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = when (paymentState) {
                            is PaymentUiState.Success -> "Faster Payment Cleared"
                            is PaymentUiState.FraudBlocked -> "Security Intercept Triggered"
                            is PaymentUiState.Loading -> "Processing..."
                            else -> "Transfer Failed"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    when (val state = paymentState) {
                        is PaymentUiState.Loading -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(color = LloydsGreen)
                                Text("Routing transaction secure protocols...")
                            }
                        }
                        is PaymentUiState.Success -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(state.message, style = MaterialTheme.typography.bodyMedium)
                                if (state.roundUpMessage.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(LloydsLightMint)
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LloydsGreen, modifier = Modifier.size(16.dp))
                                            Text(state.roundUpMessage, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = LloydsGreen))
                                        }
                                    }
                                }
                            }
                        }
                        is PaymentUiState.FraudBlocked -> {
                            Text(
                                text = state.reason,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = AlertRed
                            )
                        }
                        is PaymentUiState.Error -> {
                            Text(state.error, style = MaterialTheme.typography.bodyMedium)
                        }
                        else -> {}
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (paymentState is PaymentUiState.FraudBlocked) {
                        viewModel.clearPaymentState()
                        // Route to security tab immediately
                        viewModel.selectTab(4)
                    } else {
                        viewModel.clearPaymentState()
                        amountInput = ""
                        referenceInput = ""
                    }
                }) {
                    Text(
                        text = if (paymentState is PaymentUiState.FraudBlocked) "Review Security Hub" else "Acknowledge",
                        color = if (paymentState is PaymentUiState.FraudBlocked) AlertRed else LloydsGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

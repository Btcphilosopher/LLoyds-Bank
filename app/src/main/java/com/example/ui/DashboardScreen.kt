package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AccountEntity
import com.example.data.local.TransactionEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: BankViewModel,
    modifier: Modifier = Modifier
) {
    val accounts by viewModel.accounts.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val user by viewModel.user.collectAsState()
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val aiInsight by viewModel.aiInsight.collectAsState()
    val isGeneratingInsight by viewModel.isGeneratingInsight.collectAsState()

    var showInsightDialog by remember { mutableStateOf(false) }

    val totalBalance = accounts.sumOf { it.balance }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- User Welcome Header ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Morning,",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    )
                    Text(
                        text = user?.name ?: "Valued Customer",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                // Lloyds Premium Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(LloydsLightMint)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "CLUB LLOYDS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = LloydsGreen,
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }

        // --- Total Balance Card ---
        item {
            Card(
                shape = RoundedCornerShape(32.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                colors = CardDefaults.cardColors(containerColor = LloydsBalanceCardGreen),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            // Top right corner decorative circle representing Sleek styling
                            drawCircle(
                                color = Color.White.copy(alpha = 0.05f),
                                radius = 80.dp.toPx(),
                                center = Offset(size.width, 0f)
                            )
                        }
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL BALANCE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextLight.copy(alpha = 0.7f),
                                    letterSpacing = 1.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.1f))
                                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "PREMIER",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = TextLight,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        val balanceFormatted = String.format("%,.2f", totalBalance)
                        val parts = balanceFormatted.split(".")
                        val wholePart = parts.getOrNull(0) ?: "0"
                        val decimalPart = parts.getOrNull(1) ?: "00"

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "£$wholePart",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    letterSpacing = (-1).sp
                                )
                            )
                            Text(
                                text = ".$decimalPart",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.6f),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                ),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy((-8).dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .border(2.dp, LloydsBalanceCardGreen, CircleShape)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.1f))
                                        .border(2.dp, LloydsBalanceCardGreen, CircleShape)
                                )
                            }
                            Text(
                                text = "**** 4829",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextLight.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- Quick Action Grid ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "QUICK ACTIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSlate,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Black
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Send,
                        label = "Send",
                        onClick = { viewModel.selectTab(1) },
                        tag = "quick_send"
                    )
                    QuickActionButton(
                        icon = Icons.Default.TrendingUp,
                        label = "Goals",
                        onClick = { viewModel.selectTab(3) },
                        tag = "quick_goals"
                    )
                    QuickActionButton(
                        icon = Icons.Default.Shield,
                        label = "Shield",
                        onClick = { viewModel.selectTab(4) },
                        tag = "quick_security"
                    )
                    QuickActionButton(
                        icon = Icons.Default.Terminal,
                        label = "Specs",
                        onClick = { viewModel.selectTab(5) },
                        tag = "quick_specs"
                    )
                }
            }
        }

        // --- Gemini Financial Coach Advisory Banner ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LloydsLightMint),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.generateAiSpendingInsights()
                        showInsightDialog = true
                    }
                    .testTag("ai_insight_button")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(LloydsGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Gemini AI",
                            tint = LloydsGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Consult Lloyds AI Coach",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LloydsGreen
                            )
                        )
                        Text(
                            text = "Analyze spending habits & get custom investment plans powered by Gemini.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDark.copy(alpha = 0.7f)
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = "Open",
                        tint = LloydsGreen,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // --- Accounts Slider/Selector ---
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "YOUR ACCOUNTS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    accounts.forEach { account ->
                        val isSelected = selectedAccount?.id == account.id
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) LloydsLightMint.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) LloydsGreen else MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { viewModel.selectAccount(account) }
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = account.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) LloydsGreen else MaterialTheme.colorScheme.onBackground
                                        )
                                    )
                                    Text(
                                        text = "No. ${account.accountNumber} • SC ${account.sortCode}",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                        )
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "£${String.format("%,.2f", account.balance)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) LloydsGreen else MaterialTheme.colorScheme.onBackground
                                        )
                                    )
                                    if (account.type == "SAVINGS") {
                                        Text(
                                            text = "${account.interestRate}% AER",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = LloydsGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Custom Spending Breakdown Visualizer (Compose Canvas) ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "MONTHLY SPENDING RATIO",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stylized canvas visual bar segments
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        // 1. Bills (Groceries) 30%
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.35f)
                                .background(LloydsGreen)
                        )
                        // 2. Transfer 25%
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.25f)
                                .background(LloydsGold)
                        )
                        // 3. Travel 20%
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.20f)
                                .background(Color(0xFF2E7D32))
                        )
                        // 4. Shopping 15%
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.15f)
                                .background(Color(0xFFF57C00))
                        )
                        // 5. Rest 5%
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(0.05f)
                                .background(Color(0xFFB3261E))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Legend
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpendingLegendItem(color = LloydsGreen, label = "Groceries (35%)")
                        SpendingLegendItem(color = LloydsGold, label = "Transfers (25%)")
                        SpendingLegendItem(color = Color(0xFF2E7D32), label = "Travel (20%)")
                    }
                }
            }
        }

        // --- Recent Transaction Log (Active/Pending filters) ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVITY BREAKDOWN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextSlate,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Black
                    )
                )

                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = LloydsGreen,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { viewModel.selectTab(4) }
                )
            }
        }

        // Show only transaction of the selectedAccount or all
        val filteredTx = transactions.filter { selectedAccount == null || it.accountId == selectedAccount!!.id }
        if (filteredTx.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No recent transactions for this account.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSlate.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        } else {
            item {
                Card(
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        filteredTx.take(8).forEach { tx ->
                            TransactionListItem(tx)
                        }
                    }
                }
            }
        }
    }

    // --- Gemini Insights Dialog Sheet ---
    if (showInsightDialog) {
        AlertDialog(
            onDismissRequest = { showInsightDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Gemini Coach",
                        tint = LloydsGold
                    )
                    Text(
                        text = "Lloyds Personal Wealth Coach",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (isGeneratingInsight) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = LloydsGreen)
                            Text(
                                text = "Gemini is analyzing your secure financial footprint...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    } else {
                        Text(
                            text = aiInsight ?: "No insights generated. Try again.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showInsightDialog = false }) {
                    Text("Secure Close", color = LloydsGreen)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun RowScope.QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    tag: String,
    iconColor: Color = LloydsGreen
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .weight(1f)
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Card(
            onClick = onClick,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.8f)),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .testTag(tag),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp,
                color = TextSlate
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TransactionListItem(tx: TransactionEntity) {
    val isCredit = tx.type == "CREDIT"
    val dateString = SimpleDateFormat("dd MMM, HH:mm", Locale.UK).format(Date(tx.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { /* Select transaction detail */ }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category icon selection with sleek transparent pastel background
                val categoryColor = when (tx.category) {
                    "Salary" -> AlertGreen
                    "Groceries" -> Color(0xFF3B82F6) // Sleek Blue
                    "Bills" -> Color(0xFFF59E0B) // Sleek Amber
                    "Transfer" -> Color(0xFF10B981) // Sleek Emerald
                    "Travel" -> Color(0xFF8B5CF6) // Sleek Purple
                    "FraudAlert" -> AlertRed
                    else -> LloydsGreen
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (tx.category) {
                            "Salary" -> Icons.Default.AddCard
                            "Groceries" -> Icons.Default.ShoppingCart
                            "Bills" -> Icons.Default.ReceiptLong
                            "Transfer" -> Icons.Default.SwapHoriz
                            "Travel" -> Icons.Default.DirectionsCar
                            "FraudAlert" -> Icons.Default.Warning
                            else -> Icons.Default.CreditCard
                        },
                        contentDescription = tx.category,
                        tint = categoryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (tx.status == "FAILED") AlertRed else TextDark
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$dateString • ${tx.description}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSlate,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}£${String.format("%.2f", tx.amount)}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = when {
                            tx.status == "FAILED" -> AlertRed
                            isCredit -> AlertGreen
                            else -> TextDark
                        }
                    )
                )
                // Risk score badge if suspicious
                if (tx.isSuspicious) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(RiskMedium.copy(alpha = 0.15f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "RISK: ${String.format("%.0f", tx.riskScore)}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = RiskMedium,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SpendingLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
    }
}

@Composable
fun TransactionRow(tx: TransactionEntity) {
    val isCredit = tx.type == "CREDIT"
    val dateString = SimpleDateFormat("dd MMM, HH:mm", Locale.UK).format(Date(tx.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Category icon selection
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                tx.status == "FAILED" -> AlertRed.copy(alpha = 0.15f)
                                tx.category == "Salary" -> AlertGreen.copy(alpha = 0.15f)
                                else -> LloydsLightMint
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (tx.category) {
                            "Salary" -> Icons.Default.AddCard
                            "Groceries" -> Icons.Default.ShoppingCart
                            "Bills" -> Icons.Default.ReceiptLong
                            "Transfer" -> Icons.Default.SwapHoriz
                            "Travel" -> Icons.Default.DirectionsCar
                            "FraudAlert" -> Icons.Default.Warning
                            else -> Icons.Default.CreditCard
                        },
                        contentDescription = tx.category,
                        tint = when {
                            tx.status == "FAILED" -> AlertRed
                            tx.category == "Salary" -> AlertGreen
                            else -> LloydsGreen
                        },
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tx.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (tx.status == "FAILED") AlertRed else MaterialTheme.colorScheme.onSurface
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "$dateString • ${tx.description}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}£${String.format("%.2f", tx.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            tx.status == "FAILED" -> AlertRed
                            isCredit -> AlertGreen
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                )
                // Risk score badge if suspicious
                if (tx.isSuspicious) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(RiskMedium.copy(alpha = 0.15f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "RISK: ${String.format("%.0f", tx.riskScore)}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = RiskMedium,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

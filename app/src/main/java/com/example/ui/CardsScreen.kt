package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun CardsScreen(
    viewModel: BankViewModel,
    modifier: Modifier = Modifier
) {
    val selectedAccount by viewModel.selectedAccount.collectAsState()
    val cardPinRevealed by viewModel.cardPinRevealed.collectAsState()

    var spendLimitValue by remember { mutableFloatStateOf(2500f) }
    var contactlessActive by remember { mutableStateOf(true) }
    var onlineTxActive by remember { mutableStateOf(true) }
    var showReplaceCardDialog by remember { mutableStateOf(false) }

    // Account freeze status acts as the card freeze status
    val isFrozen = selectedAccount?.isFrozen == true

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
                    text = "CARDS MANAGEMENT",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Your Physical & Virtual Chips",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }

        // --- Gorgeous Card Graphic ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.5.dp,
                        color = if (isFrozen) AlertRed else LloydsGold.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(
                        Brush.linearGradient(
                            colors = if (isFrozen) {
                                listOf(Color(0xFF3A1211), Color(0xFF1F0A09))
                            } else {
                                listOf(LloydsGreen, Color(0xFF072C1C), Color(0xFF0F1E19))
                            },
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top row: Brand & Type
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "LLOYDS BANK",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp,
                                    color = LloydsGold
                                )
                            )
                            Text(
                                text = "VISA DEBIT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextLight.copy(alpha = 0.6f)
                                )
                            )
                        }

                        // Gold Smart Chip symbol
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(LloydsGold.copy(alpha = 0.8f))
                                .border(1.dp, TextLight.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        )
                    }

                    // Card Number
                    Text(
                        text = "4532   9011   4832   9908",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextLight,
                            letterSpacing = 2.sp
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Bottom row: Holder, Expiry & Status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CARDHOLDER NAME",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextLight.copy(alpha = 0.5f),
                                    fontSize = 8.sp
                                )
                            )
                            Text(
                                text = "DR. THOMAS HARRISON",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "EXPIRY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextLight.copy(alpha = 0.5f),
                                    fontSize = 8.sp
                                )
                            )
                            Text(
                                text = "11 / 30",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextLight
                                )
                            )
                        }

                        // Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isFrozen) AlertRed else AlertGreen)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (isFrozen) "FROZEN" else "ACTIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextLight,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- Fast Card Controls ---
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
                        text = "CARD UTILITIES",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LloydsGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    // 1. Freeze toggle
                    CardControlRow(
                        icon = if (isFrozen) Icons.Default.LockOpen else Icons.Default.Lock,
                        label = if (isFrozen) "Unfreeze Card Now" else "Freeze Card (Instant)",
                        description = "Instantly block all contactless transactions and ATM cash withdrawals.",
                        action = {
                            selectedAccount?.let { viewModel.toggleCardFreeze(it.id) }
                        },
                        tag = "card_freeze_toggle",
                        textColor = if (isFrozen) AlertGreen else AlertRed
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // 2. PIN Reveal
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Password, contentDescription = null, tint = LloydsGreen)
                                Column {
                                    Text("Reveal 4-Digit PIN", fontWeight = FontWeight.Bold)
                                    Text(
                                        "Secure authenticated access to your PIN",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.revealCardPin(!cardPinRevealed) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (cardPinRevealed) MaterialTheme.colorScheme.outlineVariant else LloydsGreen
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("pin_reveal_button")
                            ) {
                                Text(
                                    text = if (cardPinRevealed) "Mask PIN" else "Reveal PIN",
                                    color = if (cardPinRevealed) MaterialTheme.colorScheme.onSurface else TextLight,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }

                        AnimatedVisibility(visible = cardPinRevealed) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LloydsLightMint)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "8 4 9 2",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 8.sp,
                                            color = LloydsGreen
                                        )
                                    )
                                    Text(
                                        text = "This PIN will mask automatically. Keep it secure.",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = LloydsGreen.copy(alpha = 0.7f)
                                        ),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Card Limits & Settings ---
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
                        text = "SPENDING LIMITS & CONTROLS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LloydsGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    // Spend Limit Slider
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Monthly Spending Limit", fontWeight = FontWeight.Bold)
                            Text("£${spendLimitValue.toInt()}", fontWeight = FontWeight.Bold, color = LloydsGreen)
                        }
                        Slider(
                            value = spendLimitValue,
                            onValueChange = { spendLimitValue = it },
                            valueRange = 0f..5000f,
                            steps = 10,
                            colors = SliderDefaults.colors(
                                thumbColor = LloydsGreen,
                                activeTrackColor = LloydsGreen,
                                inactiveTrackColor = MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.testTag("spend_limit_slider")
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Contactless Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Contactless Purchases", fontWeight = FontWeight.Bold)
                            Text(
                                "Allow card tapping up to £100 per transaction.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                        Switch(
                            checked = contactlessActive,
                            onCheckedChange = { contactlessActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LloydsGreen,
                                checkedTrackColor = LloydsLightMint
                            ),
                            modifier = Modifier.testTag("contactless_toggle")
                        )
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Online purchases toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Online Transactions", fontWeight = FontWeight.Bold)
                            Text(
                                "Secure e-commerce authorization and subscriptions.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }
                        Switch(
                            checked = onlineTxActive,
                            onCheckedChange = { onlineTxActive = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LloydsGreen,
                                checkedTrackColor = LloydsLightMint
                            ),
                            modifier = Modifier.testTag("online_tx_toggle")
                        )
                    }
                }
            }
        }

        // --- Card replacement trigger ---
        item {
            Button(
                onClick = { showReplaceCardDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("replace_card_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CreditCardOff, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                    Text(
                        "Order Replacement Card (Lost/Stolen)",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    if (showReplaceCardDialog) {
        AlertDialog(
            onDismissRequest = { showReplaceCardDialog = false },
            title = {
                Text(
                    text = "Request Card Replacement",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text("Are you sure you want to request a replacement Visa Debit Card? This will permanently cancel your current chip (ending in *9908). Your new card will be dispatched via secure Royal Mail 1st Class and arrive within 3-5 working days.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReplaceCardDialog = false
                        // Simulate replacement logging
                    }
                ) {
                    Text("Confirm Order", color = AlertRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReplaceCardDialog = false }) {
                    Text("Cancel", color = LloydsGreen)
                }
            }
        )
    }
}

@Composable
fun CardControlRow(
    icon: ImageVector,
    label: String,
    description: String,
    action: () -> Unit,
    tag: String,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = action)
            .testTag(tag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(icon, contentDescription = null, tint = textColor)
            Column {
                Text(label, fontWeight = FontWeight.Bold, color = textColor)
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }
        }
        Icon(Icons.Default.ArrowForwardIos, contentDescription = null, modifier = Modifier.size(16.dp), tint = textColor)
    }
}

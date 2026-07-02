package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.ui.theme.*

@Composable
fun SavingsScreen(
    viewModel: BankViewModel,
    modifier: Modifier = Modifier
) {
    val savingsGoals by viewModel.savingsGoals.collectAsState()

    var showCreateGoalForm by remember { mutableStateOf(false) }

    // New Goal Form states
    var goalNameInput by remember { mutableStateOf("") }
    var goalTargetInput by remember { mutableStateOf("") }
    var isRoundUpEnabled by remember { mutableStateOf(true) }

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
                    text = "SAVINGS & INVESTMENT GOALS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Build Secure Passive Wealth",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }

        // --- Round-up Explainer Banner ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LloydsLightMint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(LloydsGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Automated Save",
                            tint = LloydsGold,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automated Save the Change",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LloydsGreen
                            )
                        )
                        Text(
                            text = "When active, payments are rounded up to the nearest £1, routing the difference directly to your selected goal! Safe & friction-free.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDark.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }

        // --- Create Goal Form Trigger ---
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCreateGoalForm = !showCreateGoalForm }
                    .testTag("create_goal_trigger_card")
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
                        Icon(Icons.Default.AddBox, contentDescription = null, tint = LloydsGreen)
                        Text("Create New Savings Target", fontWeight = FontWeight.Bold)
                    }
                    Icon(
                        imageVector = if (showCreateGoalForm) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle"
                    )
                }
            }
        }

        // --- New Savings Goal Form Content ---
        if (showCreateGoalForm) {
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
                            text = "SAVINGS TARGET DETAIL",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = LloydsGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        OutlinedTextField(
                            value = goalNameInput,
                            onValueChange = { goalNameInput = it },
                            label = { Text("What are you saving for?") },
                            leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null, tint = LloydsGreen) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_name_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = goalTargetInput,
                            onValueChange = { goalTargetInput = it },
                            label = { Text("Target Amount (£)") },
                            leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null, tint = LloydsGreen) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_target_input"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Set as Active Round-Up Goal", fontWeight = FontWeight.Bold)
                                Text(
                                    "Route automated Save-the-Change transaction spare pennies directly to this target.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                )
                            }
                            Switch(
                                checked = isRoundUpEnabled,
                                onCheckedChange = { isRoundUpEnabled = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = LloydsGreen,
                                    checkedTrackColor = LloydsLightMint
                                ),
                                modifier = Modifier.testTag("goal_roundup_switch")
                            )
                        }

                        Button(
                            onClick = {
                                val targetAmount = goalTargetInput.toDoubleOrNull() ?: 0.0
                                if (goalNameInput.isNotBlank() && targetAmount > 0.0) {
                                    viewModel.createSavingsGoal(goalNameInput, targetAmount, isRoundUpEnabled)
                                    // Reset
                                    goalNameInput = ""
                                    goalTargetInput = ""
                                    isRoundUpEnabled = true
                                    showCreateGoalForm = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("save_goal_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = LloydsGreen),
                            shape = RoundedCornerShape(12.dp),
                            enabled = goalNameInput.isNotBlank() && goalTargetInput.isNotBlank()
                        ) {
                            Text("Initialise Goal", color = TextLight, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // --- Active Targets List ---
        item {
            Text(
                text = "ACTIVE SAVINGS TARGETS",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        if (savingsGoals.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No active savings targets set. Set one above!")
                }
            }
        } else {
            items(savingsGoals) { goal ->
                val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f..1f)
                val percentText = String.format("%.0f", progress * 100)

                Card(
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = goal.name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                if (goal.isRoundUpEnabled) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = LloydsGold, modifier = Modifier.size(14.dp))
                                        Text(
                                            text = "Active Round-Up Target",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = LloydsGold,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(LloydsLightMint)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$percentText% COMPLETED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = LloydsGreen,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Indicator
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LloydsGreen,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "CURRENTLY SAVED",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 8.sp
                                    )
                                )
                                Text(
                                    text = "£${String.format("%,.2f", goal.currentAmount)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = LloydsGreen
                                    )
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "TARGET AMOUNT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                        fontSize = 8.sp
                                    )
                                )
                                Text(
                                    text = "£${String.format("%,.2f", goal.targetAmount)}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
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

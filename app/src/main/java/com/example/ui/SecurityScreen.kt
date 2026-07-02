package com.example.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FraudLogEntity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SecurityScreen(
    viewModel: BankViewModel,
    modifier: Modifier = Modifier
) {
    val fraudLogs by viewModel.fraudLogs.collectAsState()
    val user by viewModel.user.collectAsState()

    var showReportSuccessDialog by remember { mutableStateOf(false) }

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
                    text = "FRAUD & SECURITY MODULE",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Lloyds Sentinel AI Hub",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }

        // --- Active Security Shields Telemetry ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "LIVE SHIELD STATUS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LloydsGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )

                    TelemetryStatusRow(
                        label = "End-to-End Encryption (E2EE)",
                        status = "ACTIVE",
                        statusColor = AlertGreen,
                        icon = Icons.Default.Https
                    )
                    TelemetryStatusRow(
                        label = "Biometric Login / Face ID",
                        status = user?.let { if (it.isBiometricsEnabled) "ENABLED" else "DISABLED" } ?: "ENABLED",
                        statusColor = if (user?.isBiometricsEnabled == true) AlertGreen else RiskMedium,
                        icon = Icons.Default.Fingerprint
                    )
                    TelemetryStatusRow(
                        label = "Multi-Factor Authentication",
                        status = user?.let { if (it.isMfaSetup) "VERIFIED" else "SETUP REQ" } ?: "VERIFIED",
                        statusColor = if (user?.isMfaSetup == true) AlertGreen else RiskMedium,
                        icon = Icons.Default.VpnKey
                    )
                    TelemetryStatusRow(
                        label = "GCP Cloud Armor Firewall",
                        status = "DEFENDING",
                        statusColor = AlertGreen,
                        icon = Icons.Default.Shield
                    )
                }
            }
        }

        // --- Device Fingerprint Diagnostics ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "AUTHORIZED TRUSTED DEVICE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LloydsGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Google Pixel 8 Pro", fontWeight = FontWeight.Bold)
                            Text(
                                "Last authorized: Today, 09:32 • IP: 194.22.103.11",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LloydsLightMint)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "PRIMARY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = LloydsGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // --- Fraud Rules Engine Details ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "ACTIVE RULES ENGINE DETECTORS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LloydsGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    ActiveRuleRow(
                        title = "Velocity Spike Monitor",
                        desc = "Detects rapid successive card taps or instant transfers exceeding £5k in 1 hour."
                    )
                    ActiveRuleRow(
                        title = "Scam & Safe Account Strings",
                        desc = "Scans recipient fields for fraudulent words (e.g. 'crypto', 'coinbase', 'police alert')."
                    )
                    ActiveRuleRow(
                        title = "Geospatial Velocity Check",
                        desc = "Flags logins or transactions placed physically too far from physical card taps."
                    )
                }
            }
        }

        // --- Interactive Urgent Action triggers ---
        item {
            Button(
                onClick = { showReportSuccessDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("report_suspicion_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = TextLight)
                    Text("Report Suspicious Activity / Lock Accounts", color = TextLight, fontWeight = FontWeight.Bold)
                }
            }
        }

        // --- Historic Security Log & Audits ---
        item {
            Text(
                text = "SECURITY AUDIT LOGS",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    letterSpacing = 1.5.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        if (fraudLogs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No security audit logs found.")
                }
            }
        } else {
            items(fraudLogs) { log ->
                SecurityLogCard(log)
            }
        }
    }

    if (showReportSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showReportSuccessDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Shield, contentDescription = null, tint = AlertRed)
                    Text("Urgent Security Lock initiated", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Text("Your accounts have been placed under a high-security lock. All card chips have been paused, and online transfers have been temporarily deactivated. Our Lloyds Sentinel Security Fraud hotline will contact you at your registered mobile number (+44 7700 900077) within 3 minutes to verify your security state.")
            },
            confirmButton = {
                TextButton(onClick = { showReportSuccessDialog = false }) {
                    Text("Acknowledge Shield", color = AlertRed, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun TelemetryStatusRow(
    label: String,
    status: String,
    statusColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = LloydsGreen, modifier = Modifier.size(20.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(statusColor.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = status,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun ActiveRuleRow(title: String, desc: String) {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(LloydsGreen)
            )
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            desc,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            modifier = Modifier.padding(start = 14.dp, top = 2.dp)
        )
    }
}

@Composable
fun SecurityLogCard(log: FraudLogEntity) {
    val dateString = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.UK).format(Date(log.timestamp))
    val color = when (log.severity) {
        "HIGH" -> AlertRed
        "MEDIUM" -> RiskMedium
        else -> LloydsGreen
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Text(
                        text = log.ruleName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(color.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = log.severity,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = color,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Text(
                text = log.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (log.details.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = log.details,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                if (log.isActionTaken) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AlertGreen, modifier = Modifier.size(12.dp))
                        Text(
                            text = "Countermeasures Enforced",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AlertGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

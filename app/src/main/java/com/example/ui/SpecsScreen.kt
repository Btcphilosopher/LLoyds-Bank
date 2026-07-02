package com.example.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BankingSystemSpecs
import com.example.ui.theme.*

@Composable
fun SpecsScreen(
    viewModel: BankViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSpecIndex by remember { mutableStateOf(0) }

    val specs = listOf(
        SpecItem("System Architecture", BankingSystemSpecs.SYSTEM_ARCHITECTURE, "Ascii Architecture Flow Chart"),
        SpecItem("Backend Services", BankingSystemSpecs.SPRING_BOOT_MICROSERVICES, "Spring Boot Controllers & Fraud API Client"),
        SpecItem("PostgreSQL DDL", BankingSystemSpecs.POSTGRESQL_SCHEMA, "Relational Database Schema & Constraint Audits"),
        SpecItem("Fraud Sentinel AI", BankingSystemSpecs.FRAUD_MODULE_DESIGN, "Drools Rules Engine & ML Response Mitigation"),
        SpecItem("Web Admin Dashboard", BankingSystemSpecs.WEB_ADMIN_DASHBOARD, "Internal Operations & Dispute Workflows"),
        SpecItem("API OpenAPI Docs", BankingSystemSpecs.API_DOCUMENTATION, "REST Authorization, Account, & Transfer Endpoints"),
        SpecItem("PCI-DSS Security Model", BankingSystemSpecs.SECURITY_MODEL, "HSM E2EE, TLS 1.3 Certificate Pinning"),
        SpecItem("DML Sample Dataset", BankingSystemSpecs.SAMPLE_DATASET, "Initial SQL seeds for database orchestration testing"),
        SpecItem("GCP Cloud Deployment", BankingSystemSpecs.DEPLOYMENT_ARCHITECTURE, "GKE Autopilot, Spanner DB, Kafka event streaming")
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Header Section ---
        item {
            Column {
                Text(
                    text = "SPECIFICATIONS & DELIVERABLES HUB",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Lloyds Enterprise Architecture",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        }

        // --- Specs Category Selector Slider ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                specs.forEachIndexed { index, spec ->
                    val isSelected = selectedSpecIndex == index
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSpecIndex = index },
                        label = { Text(spec.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LloydsGreen,
                            selectedLabelColor = TextLight,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.testTag("spec_tab_$index")
                    )
                }
            }
        }

        // --- Active Spec Information Header ---
        val activeSpec = specs[selectedSpecIndex]
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LloydsLightMint),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = LloydsGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = activeSpec.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = LloydsGreen
                            )
                        )
                        Text(
                            text = activeSpec.desc,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextDark.copy(alpha = 0.7f)
                            )
                        )
                    }
                }
            }
        }

        // --- Monospace Terminal Code Viewer ---
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = LloydsCardDark),
                border = BorderStroke(1.dp, LloydsGold.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Terminal Header Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1E19))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFB3261E)))
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFFF57C00)))
                            Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFF2E7D32)))
                        }

                        Text(
                            text = "lbg-terminal-${activeSpec.name.lowercase().replace(" ", "-")}.spec",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextLight.copy(alpha = 0.5f),
                                fontFamily = FontFamily.Monospace
                            )
                        )

                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = LloydsGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Console Text Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = activeSpec.content.trimIndent(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextLight,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

data class SpecItem(val name: String, val content: String, val desc: String)

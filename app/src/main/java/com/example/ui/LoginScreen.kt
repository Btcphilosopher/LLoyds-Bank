package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

@Composable
fun LoginScreen(
    viewModel: BankViewModel,
    modifier: Modifier = Modifier
) {
    val loginPin by viewModel.loginPin.collectAsState()
    val loginError by viewModel.loginError.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LloydsDarkSlate)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // --- Lloyds Branding Header ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "LLOYDS BANK",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp,
                        color = LloydsGold
                    )
                )
                Text(
                    text = "Secure Digital Platform",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextLight.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // --- Custom Vector Illustration ---
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = LloydsCardDark)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lloyds_hero_banner),
                    contentDescription = "Lloyds Secure Core Illustration",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // --- Passcode Progress Indicator ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val isActive = index < loginPin.length
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(if (isActive) LloydsGold else LloydsGreen.copy(alpha = 0.4f))
                                .border(1.5.dp, LloydsGold, CircleShape)
                        )
                    }
                }

                Text(
                    text = "Enter 4-Digit Security PIN",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = TextLight,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )

                AnimatedVisibility(visible = loginError != null) {
                    Text(
                        text = loginError ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }

            // --- Tactile Numeric Keypad ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val keys = listOf(
                    listOf('1', '2', '3'),
                    listOf('4', '5', '6'),
                    listOf('7', '8', '9'),
                    listOf('B', '0', 'C') // Biometric, 0, Backspace
                )

                for (row in keys) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (key in row) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (key) {
                                            'B', 'C' -> LloydsGreen.copy(alpha = 0.2f)
                                            else -> LloydsGreen
                                        }
                                    )
                                    .clickable {
                                        when (key) {
                                            'B' -> viewModel.login("1234") // Simulate biometric click
                                            'C' -> viewModel.clearPin()
                                            else -> viewModel.appendPinChar(key)
                                        }
                                    }
                                    .testTag("pin_key_$key"),
                                contentAlignment = Alignment.Center
                            ) {
                                when (key) {
                                    'B' -> Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Biometric Login",
                                        tint = LloydsGold,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    'C' -> Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Backspace",
                                        tint = TextLight,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    else -> Text(
                                        text = key.toString(),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextLight
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // --- Secure Footnote info ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Security lock",
                    tint = LloydsGold,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "AES-GCM-256 E2EE Endpoints Active (Compliance ID: LBG-991A)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextLight.copy(alpha = 0.5f),
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

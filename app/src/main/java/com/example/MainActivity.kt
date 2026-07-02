package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: BankViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                val activeTab by viewModel.activeTab.collectAsState()

                // Support Chat sheet state
                var showSupportChat by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    if (!isLoggedIn) {
                        LoginScreen(
                            viewModel = viewModel,
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            topBar = {
                                CenterAlignedTopAppBar(
                                    title = {
                                        Text(
                                            text = "LLOYDS BANK",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 2.sp,
                                                color = LloydsGold
                                            )
                                        )
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = { viewModel.resetDemoData() }) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Reset Demo",
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    },
                                    actions = {
                                        IconButton(
                                            onClick = { showSupportChat = true },
                                            modifier = Modifier.testTag("top_bar_chat_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.SupportAgent,
                                                contentDescription = "Secure Chat Support",
                                                tint = LloydsGreen
                                            )
                                        }
                                    },
                                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.background
                                    )
                                )
                            },
                            bottomBar = {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 8.dp
                                ) {
                                    NavigationBarItem(
                                        selected = activeTab == 0,
                                        onClick = { viewModel.selectTab(0) },
                                        icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Home") },
                                        label = { Text("Home", fontSize = 10.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = LloydsGreen,
                                            selectedTextColor = LloydsGreen,
                                            indicatorColor = LloydsLightMint
                                        ),
                                        modifier = Modifier.testTag("nav_tab_home")
                                    )
                                    NavigationBarItem(
                                        selected = activeTab == 1,
                                        onClick = { viewModel.selectTab(1) },
                                        icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "Transfers") },
                                        label = { Text("Pay", fontSize = 10.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = LloydsGreen,
                                            selectedTextColor = LloydsGreen,
                                            indicatorColor = LloydsLightMint
                                        ),
                                        modifier = Modifier.testTag("nav_tab_pay")
                                    )
                                    NavigationBarItem(
                                        selected = activeTab == 2,
                                        onClick = { viewModel.selectTab(2) },
                                        icon = { Icon(Icons.Default.CreditCard, contentDescription = "Cards") },
                                        label = { Text("Cards", fontSize = 10.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = LloydsGreen,
                                            selectedTextColor = LloydsGreen,
                                            indicatorColor = LloydsLightMint
                                        ),
                                        modifier = Modifier.testTag("nav_tab_cards")
                                    )
                                    NavigationBarItem(
                                        selected = activeTab == 3,
                                        onClick = { viewModel.selectTab(3) },
                                        icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Savings") },
                                        label = { Text("Savings", fontSize = 10.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = LloydsGreen,
                                            selectedTextColor = LloydsGreen,
                                            indicatorColor = LloydsLightMint
                                        ),
                                        modifier = Modifier.testTag("nav_tab_savings")
                                    )
                                    NavigationBarItem(
                                        selected = activeTab == 4,
                                        onClick = { viewModel.selectTab(4) },
                                        icon = { Icon(Icons.Default.Shield, contentDescription = "Security") },
                                        label = { Text("Sentinel", fontSize = 10.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = LloydsGreen,
                                            selectedTextColor = LloydsGreen,
                                            indicatorColor = LloydsLightMint
                                        ),
                                        modifier = Modifier.testTag("nav_tab_sentinel")
                                    )
                                    NavigationBarItem(
                                        selected = activeTab == 5,
                                        onClick = { viewModel.selectTab(5) },
                                        icon = { Icon(Icons.Default.Terminal, contentDescription = "Specs") },
                                        label = { Text("Specs", fontSize = 10.sp) },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = LloydsGreen,
                                            selectedTextColor = LloydsGreen,
                                            indicatorColor = LloydsLightMint
                                        ),
                                        modifier = Modifier.testTag("nav_tab_specs")
                                    )
                                }
                            }
                        ) { contentPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(contentPadding)
                             ) {
                                when (activeTab) {
                                    0 -> DashboardScreen(viewModel = viewModel)
                                    1 -> PaymentsScreen(viewModel = viewModel)
                                    2 -> CardsScreen(viewModel = viewModel)
                                    3 -> SavingsScreen(viewModel = viewModel)
                                    4 -> SecurityScreen(viewModel = viewModel)
                                    5 -> SpecsScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }

                // --- Lloyds Gemini Secure AI Support Drawer Sheet ---
                if (showSupportChat) {
                    SupportChatDialog(
                        viewModel = viewModel,
                        onDismiss = { showSupportChat = false }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportChatDialog(
    viewModel: BankViewModel,
    onDismiss: () -> Unit
) {
    val chatHistory by viewModel.supportChatHistory.collectAsState()
    val isWaitingForSupport by viewModel.isWaitingForSupport.collectAsState()
    var userQuestion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(AlertGreen)
                )
                Text(
                    text = "Lloyds Secure AI Support",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Messages List Canvas
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    reverseLayout = false
                ) {
                    items(chatHistory) { msg ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (msg.isUser) 16.dp else 0.dp,
                                            bottomEnd = if (msg.isUser) 0.dp else 16.dp
                                        )
                                    )
                                    .background(
                                        if (msg.isUser) LloydsGreen
                                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                    .padding(12.dp)
                                    .widthIn(max = 240.dp)
                            ) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (msg.isUser) TextLight else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    if (isWaitingForSupport) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = LloydsGreen,
                                            strokeWidth = 2.dp
                                        )
                                        Text("Gemini is composing response...", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }

                // Input Bar Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = userQuestion,
                        onValueChange = { userQuestion = it },
                        placeholder = { Text("Ask about transfers, cards...") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    IconButton(
                        onClick = {
                            if (userQuestion.isNotBlank()) {
                                viewModel.askSupportQuestion(userQuestion)
                                userQuestion = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(LloydsGreen)
                            .testTag("chat_send_button"),
                        enabled = userQuestion.isNotBlank() && !isWaitingForSupport
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = LloydsGold
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close Chat", color = LloydsGreen, fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

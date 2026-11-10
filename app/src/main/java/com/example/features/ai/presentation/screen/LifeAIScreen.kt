package com.example.features.ai.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.core.ui.ResponsiveScreenContainer
import com.example.features.ai.presentation.viewmodel.LifeAIViewModel
import com.example.features.ai.presentation.screen.AIChatScreen
import com.example.features.ai.presentation.screen.AIMemoryScreen
import com.example.features.ai.presentation.screen.AIPlannerScreen
import com.example.features.ai.presentation.screen.AIRecommendationScreen
import com.example.core.common.LifeOSViewModel

@Composable
fun LifeAIScreen(viewModel: LifeOSViewModel) {
    val context = LocalContext.current
    val aiViewModel = remember { LifeAIViewModel(context) }
    val activeTab by aiViewModel.activeTab.collectAsState()

    val initialAiTab by viewModel.initialAiTab.collectAsState()
    LaunchedEffect(initialAiTab) {
        if (initialAiTab > 0) {
            aiViewModel.setActiveTab(initialAiTab)
            viewModel.setModuleWithAiTab(com.example.core.common.NavigationModule.LIFE_AI, 0)
        }
    }

    val tabTitles = listOf("Chat", "Planejador", "Recomendações", "Memória")
    val tabIcons = listOf(
        Icons.Default.Chat,
        Icons.Default.CalendarMonth,
        Icons.Default.TrendingUp,
        Icons.Default.Psychology
    )

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // AI Header Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Color(0xFFAB47BC), Color(0xFF4A148C))
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Ícone de Inteligência Artificial",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "🤖 LifeAI",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Powered by Gemini",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                        )
                    }
                }
                
                // Header context-based action
                if (activeTab == 0) {
                    IconButton(
                        onClick = { aiViewModel.clearChat() },
                        modifier = Modifier.testTag("clear_chat_button_cockpit")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Limpar conversa da inteligência artificial",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Material 3 Scrollable Tab Row for elegant sub-navigation
            ScrollableTabRow(
                selectedTabIndex = activeTab,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                indicator = { tabPositions ->
                    if (activeTab < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                divider = {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                },
                containerColor = Color.Transparent
            ) {
                tabTitles.forEachIndexed { index, title ->
                    val isSelected = activeTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { aiViewModel.setActiveTab(index) },
                        modifier = Modifier.testTag("ai_sub_tab_$index"),
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = tabIcons[index],
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }

            // Animated Tab Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> AIChatScreen(aiViewModel = aiViewModel)
                    1 -> AIPlannerScreen(aiViewModel = aiViewModel, mainViewModel = viewModel)
                    2 -> AIRecommendationScreen(aiViewModel = aiViewModel, mainViewModel = viewModel)
                    3 -> AIMemoryScreen(aiViewModel = aiViewModel)
                }
            }
        }
    }
}

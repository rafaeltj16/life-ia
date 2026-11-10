package com.example.features.copilot.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.features.copilot.presentation.components.DailyMissionComponent
import com.example.features.copilot.presentation.components.WeeklyReviewComponent
import com.example.features.copilot.presentation.components.CopilotSuggestionComponent
import com.lifeos.widgets.ExecutiveSummaryWidget
import com.lifeos.widgets.LifeCopilotGoalPlannerWidget
import com.lifeos.widgets.LifeCopilotQuickWidget
import com.lifeos.widgets.LifeGraphEngineWidget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CopilotScreen(
    viewModel: CopilotViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🤖 Life Copilot AI",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Central de Inteligência Autônoma & Metas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Life Copilot Summary Widget
            item {
                LifeCopilotQuickWidget()
            }

            // Executive Summary Widget
            item {
                ExecutiveSummaryWidget()
            }

            // Life Graph Engine Visualizer Widget
            item {
                LifeGraphEngineWidget()
            }

            // Goal Planner Pipeline Widget
            item {
                LifeCopilotGoalPlannerWidget(
                    onPlanExecuted = {
                        uiState.goalPlan?.let { viewModel.createPlan(it.goalPrompt) }
                    }
                )
            }

            // Daily Missions Component
            item {
                DailyMissionComponent(
                    missions = uiState.dailyMissions,
                    progress = uiState.dailyProgress,
                    totalXP = uiState.totalXP,
                    onToggleMission = { viewModel.toggleMission(it) }
                )
            }

            // Suggestions Component
            item {
                CopilotSuggestionComponent(
                    suggestions = uiState.suggestions,
                    onDismissSuggestion = { viewModel.dismissSuggestion(it) }
                )
            }

            // Weekly Review Component
            item {
                WeeklyReviewComponent(
                    reviews = uiState.weeklyReviews,
                    productivityGrade = uiState.productivityGrade
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

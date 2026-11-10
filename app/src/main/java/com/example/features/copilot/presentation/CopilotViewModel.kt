package com.example.features.copilot.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.copilot.data.CopilotRepositoryImpl
import com.example.features.copilot.data.CopilotSuggestion
import com.example.features.copilot.data.DailyMission
import com.example.features.copilot.data.GoalPlan
import com.example.features.copilot.data.WeeklyReview
import com.example.features.copilot.domain.CopilotPlanner
import com.example.features.copilot.domain.CopilotProgressEngine
import com.example.features.copilot.domain.CopilotSuggestionEngine
import com.example.features.copilot.domain.DailyMissionEngine
import com.example.features.copilot.domain.WeeklyReviewEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CopilotUiState(
    val goalPlan: GoalPlan? = null,
    val dailyMissions: List<DailyMission> = emptyList(),
    val weeklyReviews: List<WeeklyReview> = emptyList(),
    val suggestions: List<CopilotSuggestion> = emptyList(),
    val dailyProgress: Float = 0f,
    val totalXP: Int = 0,
    val productivityGrade: String = "A (Excelente)",
    val isLoading: Boolean = false
)

class CopilotViewModel : ViewModel() {

    private val repository = CopilotRepositoryImpl()

    val plannerEngine = CopilotPlanner(repository)
    val progressEngine = CopilotProgressEngine()
    val suggestionEngine = CopilotSuggestionEngine(repository)
    val weeklyReviewEngine = WeeklyReviewEngine(repository)
    val dailyMissionEngine = DailyMissionEngine(repository)

    val uiState: StateFlow<CopilotUiState> = combine(
        plannerEngine.getActivePlan(),
        dailyMissionEngine.getMissions(),
        weeklyReviewEngine.getWeeklyReviews(),
        suggestionEngine.getSuggestions()
    ) { plan, missions, reviews, suggestions ->
        CopilotUiState(
            goalPlan = plan,
            dailyMissions = missions,
            weeklyReviews = reviews,
            suggestions = suggestions,
            dailyProgress = progressEngine.calculateDailyProgress(missions),
            totalXP = progressEngine.calculateTotalXP(missions),
            productivityGrade = progressEngine.calculateProductivityGrade(reviews.firstOrNull())
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CopilotUiState(isLoading = true)
    )

    fun toggleMission(missionId: String) {
        viewModelScope.launch {
            dailyMissionEngine.toggleMission(missionId)
        }
    }

    fun dismissSuggestion(suggestionId: String) {
        viewModelScope.launch {
            suggestionEngine.dismissSuggestion(suggestionId)
        }
    }

    fun createPlan(prompt: String) {
        viewModelScope.launch {
            plannerEngine.generatePlanFromPrompt(prompt)
        }
    }

    fun refreshMissionsWithAI(context: com.example.services.gemini.GeminiUserContext) {
        viewModelScope.launch {
            dailyMissionEngine.generateMissionsWithGemini(context)
        }
    }
}

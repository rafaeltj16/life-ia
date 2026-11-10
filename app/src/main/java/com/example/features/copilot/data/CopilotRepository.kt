package com.example.features.copilot.data

import kotlinx.coroutines.flow.Flow

data class GoalPlan(
    val id: String = "plan_1",
    val goalPrompt: String = "Quero lançar meu aplicativo em 90 dias.",
    val targetDays: Int = 90,
    val projectsCount: Int = 1,
    val tasksCount: Int = 15,
    val habitsCount: Int = 2,
    val calendarBlocksCount: Int = 5,
    val focusSessionsCount: Int = 3,
    val isExecuted: Boolean = true
)

data class DailyMission(
    val id: String,
    val title: String,
    val category: String,
    val points: Int,
    val isCompleted: Boolean = false,
    val priority: String = "MÉDIA"
)

data class WeeklyReview(
    val id: String,
    val weekLabel: String,
    val productivityScore: Int, // 0 - 100
    val summary: String,
    val completedMissions: Int,
    val totalFocusMinutes: Int,
    val AIRecommendations: List<String>
)

data class CopilotSuggestion(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // "Agenda", "Foco", "Hábito", "Financeiro"
    val confidenceScore: Float // 0.0 - 1.0
)

interface CopilotRepository {
    fun getActiveGoalPlan(): Flow<GoalPlan?>
    fun getDailyMissions(): Flow<List<DailyMission>>
    fun getWeeklyReviews(): Flow<List<WeeklyReview>>
    fun getCopilotSuggestions(): Flow<List<CopilotSuggestion>>
    
    suspend fun completeMission(missionId: String)
    suspend fun createGoalPlan(prompt: String): GoalPlan
    suspend fun dismissSuggestion(suggestionId: String)
    suspend fun updateDailyMissions(missions: List<DailyMission>)
}

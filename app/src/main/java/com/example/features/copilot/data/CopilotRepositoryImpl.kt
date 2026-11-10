package com.example.features.copilot.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class CopilotRepositoryImpl : CopilotRepository {

    private val _goalPlan = MutableStateFlow<GoalPlan?>(
        GoalPlan(
            id = "plan_app_90_days",
            goalPrompt = "Quero lançar meu aplicativo em 90 dias.",
            targetDays = 90,
            projectsCount = 1,
            tasksCount = 15,
            habitsCount = 2,
            calendarBlocksCount = 5,
            focusSessionsCount = 3,
            isExecuted = true
        )
    )

    private val _dailyMissions = MutableStateFlow<List<DailyMission>>(
        listOf(
            DailyMission("m1", "Revisar wireframes e UX da Landing Page", "Projeto", 50, isCompleted = true, priority = "ALTA"),
            DailyMission("m2", "Completar 1 sessão de Foco de 25min em Kotlin", "Foco", 40, isCompleted = false, priority = "ALTA"),
            DailyMission("m3", "Registrar consumo de água (2000ml)", "Saúde", 20, isCompleted = true, priority = "MÉDIA"),
            DailyMission("m4", "Revisar relatório financeiro semanal", "Finanças", 30, isCompleted = false, priority = "MÉDIA")
        )
    )

    private val _weeklyReviews = MutableStateFlow<List<WeeklyReview>>(
        listOf(
            WeeklyReview(
                id = "w1",
                weekLabel = "Semana Atual (Semana 31)",
                productivityScore = 88,
                summary = "Excelente consistência em sessões de foco. Mapeamento de tarefas e projetos com índice de conclusão de 92%.",
                completedMissions = 18,
                totalFocusMinutes = 245,
                AIRecommendations = listOf(
                    "Agende sessões de foco no período da manhã onde seu rendimento é 35% maior.",
                    "Garanta a pausa de 10 min entre sprints de 50 min para preservar energia mental."
                )
            ),
            WeeklyReview(
                id = "w0",
                weekLabel = "Semana Passada (Semana 30)",
                productivityScore = 81,
                summary = "Bom ritmo em finanças e hábitos diários. Oito tarefas de projetos concluídas.",
                completedMissions = 15,
                totalFocusMinutes = 190,
                AIRecommendations = listOf(
                    "Aumente a ingestão diária de água nas tardes de foco intenso."
                )
            )
        )
    )

    private val _suggestions = MutableStateFlow<List<CopilotSuggestion>>(
        listOf(
            CopilotSuggestion("s1", "Reservar horário de Foco na Quinta-feira", "Sua agenda quinta à tarde está livre. Copilot recomenda reservar 2 blocos Pomodoro.", "Agenda", 0.94f),
            CopilotSuggestion("s2", "Otimização Financeira", "Gastos com transporte subiram 12% este mês. Ajustar orçamento?", "Financeiro", 0.88f),
            CopilotSuggestion("s3", "Manter sequência de Hábito de Leitura", "Você leu 4 dias seguidos! Mais 1 dia para bater seu recorde pessoal.", "Hábito", 0.95f)
        )
    )

    override fun getActiveGoalPlan(): Flow<GoalPlan?> = _goalPlan.asStateFlow()

    override fun getDailyMissions(): Flow<List<DailyMission>> = _dailyMissions.asStateFlow()

    override fun getWeeklyReviews(): Flow<List<WeeklyReview>> = _weeklyReviews.asStateFlow()

    override fun getCopilotSuggestions(): Flow<List<CopilotSuggestion>> = _suggestions.asStateFlow()

    override suspend fun completeMission(missionId: String) {
        _dailyMissions.value = _dailyMissions.value.map { mission ->
            if (mission.id == missionId) {
                mission.copy(isCompleted = !mission.isCompleted)
            } else {
                mission
            }
        }
    }

    override suspend fun createGoalPlan(prompt: String): GoalPlan {
        val newPlan = GoalPlan(
            id = "plan_${System.currentTimeMillis()}",
            goalPrompt = prompt,
            targetDays = 90,
            projectsCount = 1,
            tasksCount = 12,
            habitsCount = 2,
            calendarBlocksCount = 4,
            focusSessionsCount = 4,
            isExecuted = true
        )
        _goalPlan.value = newPlan
        return newPlan
    }

    override suspend fun dismissSuggestion(suggestionId: String) {
        _suggestions.value = _suggestions.value.filter { it.id != suggestionId }
    }

    override suspend fun updateDailyMissions(missions: List<DailyMission>) {
        _dailyMissions.value = missions
    }
}

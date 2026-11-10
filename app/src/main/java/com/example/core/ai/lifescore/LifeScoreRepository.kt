package com.example.core.ai.lifescore

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LifeScoreRepository {

    private val _currentScore = MutableStateFlow<LifeScoreResult?>(null)
    val currentScore: StateFlow<LifeScoreResult?> = _currentScore.asStateFlow()

    private val _history = MutableStateFlow(
        ScoreHistory(
            entries = listOf(
                ScoreHistoryEntry(
                    timestamp = System.currentTimeMillis() - (86400000L * 6),
                    overallScore = 68,
                    taskScore = 70,
                    habitScore = 65,
                    projectScore = 60,
                    financeScore = 75,
                    healthScore = 70,
                    note = "Início da semana"
                ),
                ScoreHistoryEntry(
                    timestamp = System.currentTimeMillis() - (86400000L * 4),
                    overallScore = 74,
                    taskScore = 78,
                    habitScore = 72,
                    projectScore = 65,
                    financeScore = 75,
                    healthScore = 80,
                    note = "Metas de tarefas cumpridas"
                ),
                ScoreHistoryEntry(
                    timestamp = System.currentTimeMillis() - (86400000L * 2),
                    overallScore = 81,
                    taskScore = 85,
                    habitScore = 80,
                    projectScore = 75,
                    financeScore = 80,
                    healthScore = 85,
                    note = "Ótimo ritmo de hábitos"
                )
            )
        )
    )
    val history: StateFlow<ScoreHistory> = _history.asStateFlow()

    fun updateCurrentScore(result: LifeScoreResult) {
        _currentScore.value = result

        val entry = ScoreHistoryEntry(
            timestamp = result.timestamp,
            overallScore = result.overallScore,
            taskScore = result.getCategoryScore(ScoreCategory.PRODUCTIVITY)?.score ?: 0,
            habitScore = result.getCategoryScore(ScoreCategory.HABITS)?.score ?: 0,
            projectScore = result.getCategoryScore(ScoreCategory.PROJECTS)?.score ?: 0,
            financeScore = result.getCategoryScore(ScoreCategory.FINANCE)?.score ?: 0,
            healthScore = result.getCategoryScore(ScoreCategory.HEALTH)?.score ?: 0
        )

        val updatedEntries = _history.value.entries.toMutableList()
        updatedEntries.add(entry)
        val trimmed = updatedEntries.sortedByDescending { it.timestamp }.take(30)
        _history.value = ScoreHistory(entries = trimmed)
    }

    fun clearHistory() {
        _history.value = ScoreHistory()
    }
}

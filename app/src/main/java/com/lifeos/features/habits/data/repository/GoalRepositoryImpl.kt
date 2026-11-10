package com.lifeos.features.habits.data.repository

import com.example.core.common.GoalItem
import com.lifeos.features.habits.domain.repository.GoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GoalRepositoryImpl : GoalRepository {
    private val localGoals = mutableMapOf<String, MutableList<GoalItem>>()

    private fun getDefaultGoals(userId: String): MutableList<GoalItem> {
        return mutableListOf(
            GoalItem(
                title = "🏃 Maratona de Berlim (42K)",
                targetValue = "42 km",
                progressValue = "25 km",
                progressPercent = 0.59f,
                userId = userId
            ),
            GoalItem(
                title = "📚 Ler 24 livros no ano",
                targetValue = "24 livros",
                progressValue = "12 livros",
                progressPercent = 0.50f,
                userId = userId
            ),
            GoalItem(
                title = "💰 Reserva de Emergência",
                targetValue = "R$ 30.000",
                progressValue = "R$ 15.000",
                progressPercent = 0.50f,
                userId = userId
            )
        )
    }

    override suspend fun getGoals(userId: String): List<GoalItem> = withContext(Dispatchers.IO) {
        return@withContext localGoals.getOrPut(userId) { getDefaultGoals(userId) }
    }

    override suspend fun saveGoal(goal: GoalItem) {
        withContext(Dispatchers.IO) {
            val userId = goal.userId ?: ""
            val list = localGoals.getOrPut(userId) { getDefaultGoals(userId) }
            val index = list.indexOfFirst { it.id == goal.id }
            if (index != -1) {
                list[index] = goal
            } else {
                list.add(goal)
            }
        }
    }

    override suspend fun deleteGoal(userId: String, goalId: String) = withContext(Dispatchers.IO) {
        val list = localGoals[userId]
        list?.removeAll { it.id == goalId }
        Unit
    }
}

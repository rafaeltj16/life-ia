package com.example.features.goals.data

import com.example.features.goals.domain.Goal
import com.example.features.goals.domain.GoalStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class GoalRepositoryImpl : GoalRepository {

    private val goalsState = MutableStateFlow<List<GoalEntity>>(
        listOf(
            GoalEntity(
                id = "goal_1",
                title = "Lançar o LifeOS AI",
                description = "Lançar a versão completa do assistente e dashboard",
                category = "Trabalho",
                targetProgress = 100,
                currentProgress = 68,
                targetDateMillis = System.currentTimeMillis() + 15L * 24 * 3600 * 1000,
                priority = 1,
                statusString = GoalStatus.IN_PROGRESS.name
            ),
            GoalEntity(
                id = "goal_2",
                title = "Aprender Jetpack Compose Avançado",
                description = "Dominar Canvas, Layouts Customizados e Performance",
                category = "Estudos",
                targetProgress = 10,
                currentProgress = 7,
                targetDateMillis = System.currentTimeMillis() + 30L * 24 * 3600 * 1000,
                priority = 2,
                statusString = GoalStatus.IN_PROGRESS.name
            )
        )
    )

    override fun getGoals(): Flow<List<Goal>> {
        return goalsState.asStateFlow().map { entities ->
            entities.map { GoalMapper.toDomain(it) }
        }
    }

    override suspend fun getGoalById(id: String): Goal? {
        val entity = goalsState.value.find { it.id == id } ?: return null
        return GoalMapper.toDomain(entity)
    }

    override suspend fun insertGoal(goal: Goal) {
        val current = goalsState.value.toMutableList()
        current.add(0, GoalMapper.toEntity(goal))
        goalsState.value = current
    }

    override suspend fun updateProgress(id: String, progress: Int) {
        val current = goalsState.value.toMutableList()
        val index = current.indexOfFirst { it.id == id }
        if (index != -1) {
            val old = current[index]
            val newProgress = progress.coerceIn(0, old.targetProgress)
            val newStatus = if (newProgress >= old.targetProgress) GoalStatus.COMPLETED.name else old.statusString
            current[index] = old.copy(currentProgress = newProgress, statusString = newStatus)
            goalsState.value = current
        }
    }

    override suspend fun deleteGoal(id: String) {
        val current = goalsState.value.toMutableList()
        current.removeAll { it.id == id }
        goalsState.value = current
    }
}

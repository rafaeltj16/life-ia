package com.example.features.goals.domain

import com.example.features.goals.data.GoalRepository

class GetGoalsUseCase(private val repository: GoalRepository) {
    operator fun invoke() = repository.getGoals()
}

class CreateGoalUseCase(private val repository: GoalRepository) {
    suspend operator fun invoke(goal: Goal) = repository.insertGoal(goal)
}

class UpdateGoalProgressUseCase(private val repository: GoalRepository) {
    suspend operator fun invoke(goalId: String, newProgress: Int) = repository.updateProgress(goalId, newProgress)
}

class DeleteGoalUseCase(private val repository: GoalRepository) {
    suspend operator fun invoke(goalId: String) = repository.deleteGoal(goalId)
}

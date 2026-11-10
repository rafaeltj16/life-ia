package com.example.features.goals.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.goals.data.GoalRepository
import com.example.features.goals.data.GoalRepositoryImpl
import com.example.features.goals.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class GoalsUiState(
    val goals: List<Goal> = emptyList(),
    val overallProgress: Int = 0,
    val selectedGoal: Goal? = null,
    val isLoading: Boolean = false
)

class GoalsViewModel(
    private val repository: GoalRepository = GoalRepositoryImpl(),
    private val getGoalsUseCase: GetGoalsUseCase = GetGoalsUseCase(repository),
    private val createGoalUseCase: CreateGoalUseCase = CreateGoalUseCase(repository),
    private val updateGoalProgressUseCase: UpdateGoalProgressUseCase = UpdateGoalProgressUseCase(repository),
    private val deleteGoalUseCase: DeleteGoalUseCase = DeleteGoalUseCase(repository)
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    private val progressCalculator = GoalProgressCalculator()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            getGoalsUseCase().collectLatest { goals ->
                val overall = progressCalculator.calculateOverallProgress(goals)
                _uiState.value = _uiState.value.copy(
                    goals = goals,
                    overallProgress = overall
                )
            }
        }
    }

    fun selectGoal(goal: Goal?) {
        _uiState.value = _uiState.value.copy(selectedGoal = goal)
    }

    fun createGoal(title: String, description: String, category: String, target: Int) {
        viewModelScope.launch {
            val newGoal = Goal(
                title = title,
                description = description,
                category = category,
                progress = 0f
            )
            createGoalUseCase(newGoal)
        }
    }

    fun updateProgress(goalId: String, newProgress: Int) {
        viewModelScope.launch {
            updateGoalProgressUseCase(goalId, newProgress)
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            deleteGoalUseCase(goalId)
        }
    }
}

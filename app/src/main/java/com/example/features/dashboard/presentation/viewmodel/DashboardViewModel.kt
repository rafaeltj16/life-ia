package com.example.features.dashboard.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.features.dashboard.domain.usecase.GetDashboardDataUseCase
import com.example.features.dashboard.presentation.state.DashboardUiState
import com.example.features.dashboard.presentation.event.DashboardEvent
import com.example.core.common.LifeOSViewModel
import com.example.core.common.DashboardWidget
import com.example.core.common.BaseViewModel
import com.example.core.common.Result
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    val lifeOSViewModel: LifeOSViewModel
) : BaseViewModel() {

    private val _isEditingDashboard = MutableStateFlow(false)
    val isEditingDashboard: StateFlow<Boolean> = _isEditingDashboard.asStateFlow()

    val uiState: StateFlow<DashboardUiState> = combine(
        lifeOSViewModel.tasks,
        lifeOSViewModel.habits,
        lifeOSViewModel.projects,
        lifeOSViewModel.userMetrics,
        lifeOSViewModel.transactions
    ) { _, _, _, _, _ ->
        val auth = lifeOSViewModel.authState.value
        val email = if (auth is com.example.core.common.AuthState.Authenticated) auth.email else "rafaeltj16@gmail.com"
        val userId = if (auth is com.example.core.common.AuthState.Authenticated) email else "mock-uuid-1234"
        getDashboardDataUseCase(userId)
    }
        .map { dashboardData ->
            val context = lifeOSViewModel.getApplication<android.app.Application>()
            val copilotEngine = com.example.core.ai.engine.CopilotEngine(
                rulesEngine = com.example.core.ai.engine.AIRulesEngine(),
                predictionEngine = com.example.core.ai.prediction.PredictionEngine(),
                memoryEngine = com.example.core.ai.life_ai.MemoryEngine(context),
                lifeScoreCalculator = com.example.core.ai.score.LifeScoreCalculator(),
                momentumCalculator = com.example.core.ai.score.MomentumCalculator(),
                focusScoreCalculator = com.example.core.ai.score.FocusScoreCalculator(),
                plannerEngine = com.example.core.ai.engine.PlannerEngine()
            )
            val copilotDashboard = try {
                val coreDashboardData = com.example.core.ai.model.DashboardData(
                    pendingTasks = dashboardData.todayTasks.count { !it.isCompleted },
                    overdueTasks = dashboardData.todayTasks.count { !it.isCompleted && it.dueDate == "Atrasadas" },
                    completedHabitsToday = dashboardData.todayHabits.count { it.isCompletedToday },
                    totalHabitsToday = dashboardData.todayHabits.size,
                    currentStreak = dashboardData.streak,
                    inactiveProjects = dashboardData.projects.size,
                    waterPercentage = (lifeOSViewModel.waterDrankMl.value * 100 / 2500).coerceIn(0, 100)
                )
                copilotEngine.generateDashboard(coreDashboardData)
            } catch (e: Exception) {
                null
            }
            val updatedDashboard = if (copilotDashboard != null) {
                dashboardData.copy(dailyPlan = copilotDashboard.dailyPlan)
            } else {
                dashboardData
            }
            DashboardUiState.Success(updatedDashboard) as DashboardUiState
        }
        .catch { throwable ->
            emit(DashboardUiState.Error(throwable.localizedMessage ?: "Erro ao carregar dashboard"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )

    fun toggleEditDashboard(isEditing: Boolean) {
        _isEditingDashboard.value = isEditing
    }

    fun loadMetrics(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = safeCall {
                getDashboardDataUseCase(userId)
            }
            _isLoading.value = false
            when (result) {
                is Result.Success -> {
                    lifeOSViewModel.showToast("Métricas carregadas com sucesso!", com.example.core.common.ToastType.SUCCESS)
                }
                is Result.Error -> {
                    handleException(result.throwable)
                    lifeOSViewModel.showToast("Erro ao carregar métricas: ${result.throwable.localizedMessage}", com.example.core.common.ToastType.ERROR)
                }
                Result.Loading -> {
                    _isLoading.value = true
                }
            }
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.Refresh -> {
                // Refresh logic if needed
            }
            is DashboardEvent.Retry -> {
                // Retry loading dashboard or AI action
                lifeOSViewModel.generateAITasksSuggestions()
            }
            is DashboardEvent.OpenTask -> {
                // Let OpenTask toggle the task to keep the UI interactive and fully functional
                lifeOSViewModel.toggleTask(event.id)
            }
            is DashboardEvent.OpenHabit -> {
                lifeOSViewModel.showToast("Hábito ${event.id} aberto", com.example.core.common.ToastType.INFO)
            }
            is DashboardEvent.CompleteHabit -> {
                lifeOSViewModel.toggleHabit(event.id)
            }
        }
    }

    fun moveWidgetUp(id: String) {
        lifeOSViewModel.moveWidgetUp(id)
    }

    fun moveWidgetDown(id: String) {
        lifeOSViewModel.moveWidgetDown(id)
    }
}

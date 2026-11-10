package com.example.features.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.features.calendar.presentation.screen.AgendaScreen
import com.example.features.calendar.presentation.screen.CalendarScreen
import com.lifeos.features.tasks.presentation.screen.TasksScreen
import com.example.features.projects.presentation.screen.ProjectsScreen
import com.lifeos.features.habits.presentation.screen.HabitsScreen
import com.lifeos.features.habits.presentation.screen.GoalsScreen
import com.example.features.finance.presentation.screen.FinancesScreen
import com.example.features.health.presentation.screen.HealthWorkoutScreen
import com.example.features.ai.presentation.screen.LifeAIScreen
import com.example.features.settings.presentation.screen.SettingsScreen
import com.example.core.common.LifeOSViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: LifeOSViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(220)) }
    ) {
        composable(Routes.DASHBOARD) {
            val dashboardViewModel = remember(viewModel) {
                val context = viewModel.getApplication<android.app.Application>()
                val database = com.example.core.database.AppDatabase.getDatabase(context)
                val taskRepository = com.lifeos.features.tasks.data.repository.TaskRepositoryImpl(
                    database.taskDao(),
                    context
                )
                val habitRepository = com.lifeos.features.habits.data.repository.HabitRepositoryImpl()
                val projectRepository = com.example.features.projects.data.repository.ProjectRepositoryImpl(
                    database.projectDao(),
                    context
                )
                val financeRepository = com.example.features.finance.data.repository.FinanceRepositoryImpl()

                val dashboardRepository = com.example.features.dashboard.data.repository.DashboardRepositoryImpl(
                    localDataSource = database.dashboardDao(),
                    remoteDataSource = com.example.features.dashboard.data.remote.SupabaseDashboardDataSource(),
                    taskRepository = taskRepository,
                    habitRepository = habitRepository,
                    projectRepository = projectRepository,
                    financeRepository = financeRepository,
                    getCurrentUserId = {
                        val auth = viewModel.authState.value
                        if (auth is com.example.core.common.AuthState.Authenticated) auth.email else "mock-uuid-1234"
                    }
                )

                val getDashboardDataUseCase = com.example.features.dashboard.domain.usecase.GetDashboardDataUseCase(
                    dashboardRepository = dashboardRepository
                )
                com.example.features.dashboard.presentation.viewmodel.DashboardViewModel(getDashboardDataUseCase, viewModel)
            }
            com.example.features.dashboard.presentation.screen.DashboardScreen(viewModel = dashboardViewModel)
        }

        composable(Routes.AGENDA) {
            AgendaScreen(viewModel = viewModel)
        }

        composable(Routes.CALENDAR) {
            CalendarScreen()
        }

        composable(Routes.TASKS) {
            TasksScreen(viewModel = viewModel)
        }

        composable(Routes.PROJECTS) {
            ProjectsScreen(viewModel = viewModel)
        }

        composable(Routes.HABITS) {
            HabitsScreen(viewModel = viewModel)
        }

        composable(Routes.GOALS) {
            GoalsScreen()
        }

        composable(Routes.FINANCES) {
            FinancesScreen(viewModel = viewModel)
        }

        composable(Routes.HEALTH) {
            HealthWorkoutScreen(viewModel = viewModel)
        }

        composable(Routes.LIFE_AI) {
            LifeAIScreen(viewModel = viewModel)
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(viewModel = viewModel)
        }
    }
}

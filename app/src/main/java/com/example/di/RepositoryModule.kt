package com.example.di

import android.content.Context
import com.lifeos.features.tasks.domain.repository.TaskRepository
import com.example.features.projects.domain.repository.ProjectRepository
import com.example.features.projects.data.repository.ProjectRepositoryImpl
import com.lifeos.features.habits.domain.repository.HabitRepository
import com.lifeos.features.habits.data.repository.HabitRepositoryImpl
import com.lifeos.features.habits.domain.repository.GoalRepository
import com.lifeos.features.habits.data.repository.GoalRepositoryImpl
import com.example.features.auth.domain.repository.AuthRepository
import com.example.features.auth.data.repository.AuthRepositoryImpl
import com.example.features.auth.domain.repository.UserRepository
import com.example.features.auth.data.repository.UserRepositoryImpl
import com.example.features.finance.domain.repository.FinanceRepository
import com.example.features.finance.data.repository.FinanceRepositoryImpl
import com.example.core.ai.repository.AIRepository
import com.example.features.ai.data.repository.AIRepositoryImpl
import com.example.features.dashboard.domain.repository.DashboardRepository
import com.example.features.dashboard.data.repository.DashboardRepositoryImpl

class RepositoryModule(
    private val context: Context,
    private val databaseModule: DatabaseModule
) {
    val taskRepository: TaskRepository by lazy {
        com.lifeos.features.tasks.data.repository.TaskRepositoryImpl(databaseModule.taskDao, context)
    }

    val projectRepository: ProjectRepository by lazy {
        ProjectRepositoryImpl(databaseModule.projectDao, context)
    }

    val habitRepository: HabitRepository by lazy {
        HabitRepositoryImpl()
    }

    val goalRepository: GoalRepository by lazy {
        GoalRepositoryImpl()
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl()
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl()
    }

    val financeRepository: FinanceRepository by lazy {
        FinanceRepositoryImpl()
    }

    val aiRepository: AIRepository by lazy {
        AIRepositoryImpl(context)
    }

    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepositoryImpl(
            localDataSource = databaseModule.dashboardDao,
            remoteDataSource = com.example.features.dashboard.data.remote.SupabaseDashboardDataSource(),
            taskRepository = taskRepository,
            habitRepository = habitRepository,
            projectRepository = projectRepository,
            financeRepository = financeRepository,
            getCurrentUserId = { "mock-uuid-1234" }
        )
    }

    val geminiRepository: com.example.services.gemini.GeminiRepository by lazy {
        com.example.services.gemini.GeminiRepositoryImpl()
    }

    val habitTrackerRepository: com.lifeos.features.habits.domain.repository.HabitTrackerRepository by lazy {
        com.lifeos.features.habits.data.repository.RoomHabitTrackerRepositoryImpl(
            habitDao = databaseModule.lifeHabitDao,
            habitLogDao = databaseModule.lifeHabitLogDao
        )
    }
}

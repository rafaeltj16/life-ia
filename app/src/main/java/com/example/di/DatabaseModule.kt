package com.example.di

import android.content.Context
import com.example.core.database.AppDatabase
import com.example.core.database.TaskDao
import com.example.core.database.ProjectDao
import com.example.features.dashboard.data.local.DashboardDao

import com.lifeos.core.database.LifeOSDatabase
import com.lifeos.core.database.dao.LifeHabitDao
import com.lifeos.core.database.dao.LifeHabitLogDao

class DatabaseModule(private val context: Context) {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    val lifeDatabase: LifeOSDatabase by lazy {
        LifeOSDatabase.getInstance(context)
    }

    val taskDao: TaskDao by lazy {
        database.taskDao()
    }

    val taskRepository: com.example.core.database.TaskRepository by lazy {
        com.example.core.database.TaskRepository(taskDao)
    }

    val projectDao: ProjectDao by lazy {
        database.projectDao()
    }

    val dashboardDao: DashboardDao by lazy {
        database.dashboardDao()
    }

    val lifeHabitDao: LifeHabitDao by lazy {
        lifeDatabase.habitDao()
    }

    val lifeHabitLogDao: LifeHabitLogDao by lazy {
        lifeDatabase.habitLogDao()
    }
}

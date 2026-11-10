package com.lifeos.core_ai.provider

import com.lifeos.tasks.domain.model.Task

interface TaskProvider {

    suspend fun getPendingTasks(): List<Task>

    suspend fun getOverdueTasks(): List<Task>

    suspend fun getTodayTasks(): List<Task>

}

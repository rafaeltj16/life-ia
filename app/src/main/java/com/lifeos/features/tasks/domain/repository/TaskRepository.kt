package com.lifeos.features.tasks.domain.repository

import com.example.core.common.SubTask
import com.example.core.common.Task
import com.example.core.common.TaskItem
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun getTodayTasks(): List<Task>

    suspend fun save(task: Task)

    suspend fun update(task: Task)

    suspend fun delete(id: String)

    // Additional methods supporting feature requirements
    fun getTasksFlow(): Flow<List<TaskItem>>
    suspend fun getTasks(userId: String): List<TaskItem>
    suspend fun saveTask(userId: String, task: TaskItem)
    suspend fun deleteTask(userId: String, taskId: String)
    suspend fun getSubTasks(taskId: String): List<SubTask>
    suspend fun saveSubTask(subTask: SubTask)
    suspend fun deleteSubTask(taskId: String, subTaskId: String)
    suspend fun syncWithRemote(userId: String)
}

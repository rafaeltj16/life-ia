package com.lifeos.features.tasks.data.datasource

import com.example.core.database.SubTaskEntity
import com.example.core.database.TaskDao
import com.example.core.database.TaskEntity
import com.example.core.database.TaskWithSubTasks
import kotlinx.coroutines.flow.Flow

class TaskLocalDataSource(private val dao: TaskDao) {
    fun getTasksWithSubTasksFlow(): Flow<List<TaskWithSubTasks>> = dao.getTasksWithSubTasksFlow()
    suspend fun getTasksWithSubTasks(): List<TaskWithSubTasks> = dao.getTasksWithSubTasks()
    suspend fun insertTask(task: TaskEntity) = dao.insertTask(task)
    suspend fun insertTasks(tasks: List<TaskEntity>) = dao.insertTasks(tasks)
    suspend fun insertSubTasks(subTasks: List<SubTaskEntity>) = dao.insertSubTasks(subTasks)
    suspend fun deleteTask(taskId: String) = dao.deleteTask(taskId)
    suspend fun deleteSubTask(subTaskId: String) = dao.deleteSubTask(subTaskId)
    suspend fun deleteSubTasksForTask(taskId: String) = dao.deleteSubTasksForTask(taskId)
    suspend fun clearTasks() = dao.clearTasks()
    suspend fun clearSubTasks() = dao.clearSubTasks()
}

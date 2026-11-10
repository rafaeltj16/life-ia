package com.lifeos.features.tasks.data.datasource

import com.example.core.network.SupabaseServiceClient
import com.example.core.network.SupabaseSubTask
import com.example.core.network.SupabaseTask

class TaskRemoteDataSource {
    suspend fun getTasks(userId: String): List<SupabaseTask> {
        return SupabaseServiceClient.getTasks(userId)
    }

    suspend fun getSubTasks(taskId: String): List<SupabaseSubTask> {
        return SupabaseServiceClient.getSubTasks(taskId)
    }

    suspend fun saveTask(task: SupabaseTask) {
        SupabaseServiceClient.insertTask(task)
    }

    suspend fun deleteTask(userId: String, taskId: String) {
        SupabaseServiceClient.deleteTask(userId, taskId)
    }

    suspend fun saveSubTask(subTask: SupabaseSubTask) {
        SupabaseServiceClient.insertSubTask(subTask)
    }

    suspend fun deleteSubTask(taskId: String, subTaskId: String) {
        SupabaseServiceClient.deleteSubTask(taskId, subTaskId)
    }
}

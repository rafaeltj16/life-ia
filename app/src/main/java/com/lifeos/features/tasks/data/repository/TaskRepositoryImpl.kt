package com.lifeos.features.tasks.data.repository

import android.content.Context
import android.util.Log
import com.example.core.common.SubTask
import com.example.core.common.Task
import com.example.core.common.TaskItem
import com.example.core.common.TaskPriority
import com.example.core.common.TaskStatus
import com.example.core.database.SubTaskEntity
import com.example.core.database.TaskDao
import com.example.core.database.TaskEntity
import com.example.core.database.TaskWithSubTasks
import com.example.core.network.SupabaseSubTask
import com.example.core.network.SupabaseTask
import com.example.core.sync.SyncManager
import com.lifeos.features.tasks.data.datasource.TaskLocalDataSource
import com.lifeos.features.tasks.data.datasource.TaskRemoteDataSource
import com.lifeos.features.tasks.domain.repository.TaskRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TaskRepositoryImpl(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val syncManager: SyncManager
) : TaskRepository {

    constructor(dao: TaskDao, context: Context) : this(
        localDataSource = TaskLocalDataSource(dao),
        remoteDataSource = TaskRemoteDataSource(),
        syncManager = SyncManager.getInstance(context)
    )

    private val TAG = "TaskRepository"

    override suspend fun getTodayTasks(): List<Task> {
        return getTasks("")
    }

    override suspend fun save(task: Task) {
        saveTask("", task)
    }

    override suspend fun update(task: Task) {
        saveTask("", task)
    }

    override suspend fun delete(id: String) {
        deleteTask("", id)
    }

    private fun mapToDomain(relation: TaskWithSubTasks): TaskItem {
        val task = relation.task
        val mappedPriority = try {
            TaskPriority.valueOf(task.priority.uppercase())
        } catch (e: Exception) {
            TaskPriority.MEDIUM
        }
        val mappedStatus = try {
            TaskStatus.valueOf(task.status.uppercase())
        } catch (e: Exception) {
            if (task.isCompleted) TaskStatus.COMPLETED else TaskStatus.TO_DO
        }
        val tagsList = task.tags.split(",").filter { it.isNotBlank() }

        return TaskItem(
            id = task.id,
            title = task.title,
            description = task.description,
            category = task.category,
            isCompleted = task.isCompleted,
            dueDate = task.dueDate,
            priority = mappedPriority,
            status = mappedStatus,
            tags = tagsList,
            isFavorite = task.isFavorite,
            subTasks = relation.subTasks.map {
                SubTask(
                    id = it.id,
                    taskId = it.taskId,
                    title = it.title,
                    completed = it.completed
                )
            },
            projectId = task.projectId
        )
    }

    override fun getTasksFlow(): Flow<List<TaskItem>> {
        return localDataSource.getTasksWithSubTasksFlow().map { list ->
            list.map { mapToDomain(it) }
        }
    }

    override suspend fun getTasks(userId: String): List<TaskItem> = withContext(Dispatchers.IO) {
        val localTasks = localDataSource.getTasksWithSubTasks()
        if (localTasks.isNotEmpty()) {
            return@withContext localTasks.map { mapToDomain(it) }
        }

        try {
            syncWithRemote(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Failed remote sync on getTasks", e)
        }
        return@withContext localDataSource.getTasksWithSubTasks().map { mapToDomain(it) }
    }

    override suspend fun saveTask(userId: String, task: TaskItem): Unit = withContext(Dispatchers.IO) {
        val taskEntity = TaskEntity(
            id = task.id,
            title = task.title,
            description = task.description,
            category = task.category,
            isCompleted = task.isCompleted,
            dueDate = task.dueDate,
            priority = task.priority.name,
            status = task.status.name,
            tags = task.tags.joinToString(","),
            isFavorite = task.isFavorite,
            projectId = task.projectId
        )
        localDataSource.insertTask(taskEntity)

        val subTaskEntities = task.subTasks.map {
            SubTaskEntity(
                id = it.id,
                taskId = it.taskId,
                title = it.title,
                completed = it.completed
            )
        }
        localDataSource.insertSubTasks(subTaskEntities)

        val sTask = SupabaseTask(
            id = task.id,
            userId = userId,
            title = task.title,
            description = if (task.tags.isNotEmpty()) task.tags.joinToString(",") else null,
            status = task.status.name,
            priority = task.priority.name,
            category = task.category,
            projectId = task.projectId,
            dueDate = task.dueDate,
            completedAt = if (task.isCompleted) "now" else null,
            favorite = task.isFavorite
        )

        syncManager.enqueueSync(userId, "save_task", sTask)

        task.subTasks.forEach { sub ->
            saveSubTask(sub)
        }
    }

    override suspend fun deleteTask(userId: String, taskId: String): Unit = withContext(Dispatchers.IO) {
        localDataSource.deleteSubTasksForTask(taskId)
        localDataSource.deleteTask(taskId)

        syncManager.enqueueSync(userId, "delete_task", taskId)
    }

    override suspend fun getSubTasks(taskId: String): List<SubTask> = withContext(Dispatchers.IO) {
        val relation = localDataSource.getTasksWithSubTasks().find { it.task.id == taskId }
        if (relation != null) {
            return@withContext relation.subTasks.map {
                SubTask(
                    id = it.id,
                    taskId = it.taskId,
                    title = it.title,
                    completed = it.completed
                )
            }
        }
        return@withContext emptyList()
    }

    override suspend fun saveSubTask(subTask: SubTask): Unit = withContext(Dispatchers.IO) {
        val entity = SubTaskEntity(
            id = subTask.id,
            taskId = subTask.taskId,
            title = subTask.title,
            completed = subTask.completed
        )
        localDataSource.insertSubTasks(listOf(entity))

        val sSub = SupabaseSubTask(
            id = subTask.id,
            taskId = subTask.taskId,
            title = subTask.title,
            completed = subTask.completed
        )
        syncManager.enqueueSync("", "save_subtask", sSub)
    }

    override suspend fun deleteSubTask(taskId: String, subTaskId: String): Unit = withContext(Dispatchers.IO) {
        localDataSource.deleteSubTask(subTaskId)

        syncManager.enqueueSync("", "delete_subtask", "$taskId:$subTaskId")
    }

    override suspend fun syncWithRemote(userId: String): Unit = withContext(Dispatchers.IO) {
        try {
            val supabaseTasks = remoteDataSource.getTasks(userId)
            val taskEntities = supabaseTasks.map { sTask ->
                val isCompleted = sTask.completedAt != null || sTask.status == "COMPLETED"
                val priorityStr = sTask.priority
                val statusStr = sTask.status
                val tagsStr = sTask.description ?: ""

                TaskEntity(
                    id = sTask.id,
                    title = sTask.title,
                    category = sTask.category,
                    isCompleted = isCompleted,
                    dueDate = sTask.dueDate ?: "Hoje",
                    priority = priorityStr,
                    status = statusStr,
                    tags = tagsStr,
                    isFavorite = sTask.favorite,
                    projectId = sTask.projectId
                )
            }

            val subTaskEntities = mutableListOf<SubTaskEntity>()
            supabaseTasks.forEach { sTask ->
                try {
                    val sSubTasks = remoteDataSource.getSubTasks(sTask.id)
                    sSubTasks.forEach { sSub ->
                        subTaskEntities.add(
                            SubTaskEntity(
                                id = sSub.id,
                                taskId = sSub.taskId,
                                title = sSub.title,
                                completed = sSub.completed
                            )
                        )
                    }
                } catch (subEx: Exception) {
                    Log.e(TAG, "Failed to sync subtasks for task: ${sTask.id}", subEx)
                }
            }

            localDataSource.clearTasks()
            localDataSource.clearSubTasks()

            if (taskEntities.isNotEmpty()) {
                localDataSource.insertTasks(taskEntities)
            }
            if (subTaskEntities.isNotEmpty()) {
                localDataSource.insertSubTasks(subTaskEntities)
            }

            Log.i(TAG, "Successfully synced ${taskEntities.size} tasks and ${subTaskEntities.size} subtasks from Supabase to Room.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed syncWithRemote. Staying with offline Room data.", e)
        }
    }
}

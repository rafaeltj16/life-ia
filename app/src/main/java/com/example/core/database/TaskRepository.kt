package com.example.core.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Repository class that abstracts [TaskDao] operations and provides a clean, reactive API
 * for ViewModels to interact with the local Room task database.
 *
 * Adheres to standard Android architecture principles:
 * - Exposes reactive streams ([Flow]) for observing task state.
 * - Dispatches mutating database operations on [Dispatchers.IO].
 */
class TaskRepository(
    private val taskDao: TaskDao
) {

    /**
     * Observes all tasks ordered by creation date descending.
     */
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    /**
     * Observes all pending (uncompleted) tasks ordered by priority and due date.
     */
    val pendingTasks: Flow<List<TaskEntity>> = taskDao.getPendingTasks()

    /**
     * Observes all completed tasks.
     */
    val completedTasks: Flow<List<TaskEntity>> = taskDao.getCompletedTasks()

    /**
     * Observes count of pending tasks.
     */
    val pendingTasksCount: Flow<Int> = taskDao.getPendingTasksCount()

    /**
     * Observes count of completed tasks.
     */
    val completedTasksCount: Flow<Int> = taskDao.getCompletedTasksCount()

    /**
     * Observes a specific task by ID.
     */
    fun getTaskById(taskId: String): Flow<TaskEntity?> {
        return taskDao.getTaskById(taskId)
    }

    /**
     * Fetches a one-shot direct snapshot of a task by ID.
     */
    suspend fun getTaskByIdDirect(taskId: String): TaskEntity? = withContext(Dispatchers.IO) {
        taskDao.getTaskByIdDirect(taskId)
    }

    /**
     * Observes tasks filtered by priority level ("HIGH", "MEDIUM", "LOW").
     */
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByPriority(priority)
    }

    /**
     * Observes tasks filtered by due date.
     */
    fun getTasksByDueDate(dueDate: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByDueDate(dueDate)
    }

    /**
     * Observes tasks filtered by category.
     */
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByCategory(category)
    }

    /**
     * Observes tasks belonging to a specific project.
     */
    fun getTasksByProject(projectId: String): Flow<List<TaskEntity>> {
        return taskDao.getTasksByProject(projectId)
    }

    /**
     * Fetches all tasks as a list snapshot.
     */
    suspend fun getAllTasksSnapshot(): List<TaskEntity> = withContext(Dispatchers.IO) {
        taskDao.getAllTasksList()
    }

    /**
     * Helper to create and insert a new task with given parameters.
     */
    suspend fun createTask(
        title: String,
        description: String = "",
        priority: String = "MEDIUM",
        dueDate: String = "Hoje",
        category: String = "Geral",
        tags: String = "",
        projectId: String? = null
    ): TaskEntity = withContext(Dispatchers.IO) {
        val task = TaskEntity(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            priority = priority,
            dueDate = dueDate,
            category = category,
            isCompleted = false,
            status = "TO_DO",
            tags = tags,
            isFavorite = false,
            projectId = projectId,
            createdAt = System.currentTimeMillis()
        )
        taskDao.insertTask(task)
        task
    }

    /**
     * Inserts or replaces a task entity in the database.
     */
    suspend fun insertTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.insertTask(task)
    }

    /**
     * Inserts or replaces a collection of tasks in the database.
     */
    suspend fun insertTasks(tasks: List<TaskEntity>) = withContext(Dispatchers.IO) {
        taskDao.insertTasks(tasks)
    }

    /**
     * Updates an existing task entity.
     */
    suspend fun updateTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.updateTask(task)
    }

    /**
     * Updates the completion state of a task by ID.
     */
    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean) = withContext(Dispatchers.IO) {
        taskDao.updateTaskCompletion(taskId, isCompleted)
    }

    /**
     * Toggles the completion state of a task.
     * Returns the new completion state, or null if task not found.
     */
    suspend fun toggleTaskCompletion(taskId: String): Boolean? = withContext(Dispatchers.IO) {
        val task = taskDao.getTaskByIdDirect(taskId) ?: return@withContext null
        val newCompleted = !task.isCompleted
        taskDao.updateTaskCompletion(taskId, newCompleted)
        newCompleted
    }

    /**
     * Deletes a task by entity reference.
     */
    suspend fun deleteTask(task: TaskEntity) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(task)
    }

    /**
     * Alias for [deleteTask].
     */
    suspend fun delete(task: TaskEntity) = deleteTask(task)

    /**
     * Deletes a task by ID.
     */
    suspend fun deleteTaskById(taskId: String) = withContext(Dispatchers.IO) {
        taskDao.deleteTask(taskId)
    }

    /**
     * Alias for [deleteTaskById].
     */
    suspend fun deleteById(taskId: String) = deleteTaskById(taskId)

    /**
     * Deletes all completed tasks from the database.
     */
    suspend fun deleteCompletedTasks(): Int = withContext(Dispatchers.IO) {
        taskDao.deleteCompletedTasks()
    }

    /**
     * Clears all tasks from the database.
     */
    suspend fun clearTasks() = withContext(Dispatchers.IO) {
        taskDao.clearTasks()
    }
}

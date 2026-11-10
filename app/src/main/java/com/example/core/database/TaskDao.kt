package com.example.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [TaskEntity] using Room annotations.
 * Supports insertion, deletion, update, and query operations for tasks in the local database.
 */
@Dao
interface TaskDao {

    // --- Insertion Operations ---

    /**
     * Inserts a single task into the local database, replacing on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    /**
     * Inserts multiple tasks into the local database, replacing on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    /**
     * Inserts a task, ignoring on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTaskIgnoreConflict(task: TaskEntity): Long

    // --- Deletion Operations ---

    /**
     * Deletes a task by entity reference.
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity)

    /**
     * Deletes a task by its unique ID.
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: String)

    /**
     * Deletes a task by its unique ID (alias).
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    /**
     * Deletes all completed tasks from the database.
     */
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks(): Int

    /**
     * Clears all tasks from the local table.
     */
    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    // --- Update Operations ---

    /**
     * Updates an existing task entity.
     */
    @Update
    suspend fun updateTask(task: TaskEntity)

    /**
     * Updates the completion state and status for a task by ID.
     */
    @Query("UPDATE tasks SET isCompleted = :isCompleted, status = CASE WHEN :isCompleted = 1 THEN 'COMPLETED' ELSE 'TO_DO' END WHERE id = :taskId")
    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean)

    // --- Query Operations ---

    /**
     * Observes all tasks ordered by creation timestamp descending.
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    /**
     * Direct query to fetch all tasks as a list.
     */
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    suspend fun getAllTasksList(): List<TaskEntity>

    /**
     * Observes a single task by its unique ID.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: String): Flow<TaskEntity?>

    /**
     * Direct lookup of a single task by its unique ID.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskByIdDirect(taskId: String): TaskEntity?

    /**
     * Observes all pending (uncompleted) tasks ordered by priority and due date.
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 0 ORDER BY CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 ELSE 4 END ASC, createdAt DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    /**
     * Observes all completed tasks ordered by completion/creation timestamp descending.
     */
    @Query("SELECT * FROM tasks WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    /**
     * Observes tasks matching a specific priority level.
     */
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: String): Flow<List<TaskEntity>>

    /**
     * Observes tasks matching a specific due date.
     */
    @Query("SELECT * FROM tasks WHERE dueDate = :dueDate ORDER BY CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 ELSE 4 END ASC")
    fun getTasksByDueDate(dueDate: String): Flow<List<TaskEntity>>

    /**
     * Observes tasks belonging to a specific category.
     */
    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>>

    /**
     * Observes tasks associated with a specific project.
     */
    @Query("SELECT * FROM tasks WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getTasksByProject(projectId: String): Flow<List<TaskEntity>>

    /**
     * Counts the total number of pending tasks.
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 0")
    fun getPendingTasksCount(): Flow<Int>

    /**
     * Counts the total number of completed tasks.
     */
    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTasksCount(): Flow<Int>

    // --- Relational SubTask Operations ---

    @Transaction
    @Query("SELECT * FROM tasks")
    fun getTasksWithSubTasksFlow(): Flow<List<TaskWithSubTasks>>

    @Transaction
    @Query("SELECT * FROM tasks")
    suspend fun getTasksWithSubTasks(): List<TaskWithSubTasks>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTasks(subTasks: List<SubTaskEntity>)

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubTasksForTask(taskId: String)

    @Query("DELETE FROM subtasks WHERE id = :subTaskId")
    suspend fun deleteSubTask(subTaskId: String)

    @Query("DELETE FROM subtasks")
    suspend fun clearSubTasks()
}

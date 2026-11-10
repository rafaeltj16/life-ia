package com.lifeos.core.database.dao

import androidx.room.*
import com.lifeos.core.database.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LifeTaskDao {
    @Query("SELECT * FROM life_tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM life_tasks WHERE id = :id LIMIT 1")
    fun getTaskById(id: String): Flow<TaskEntity?>

    @Query("SELECT * FROM life_tasks WHERE isCompleted = 0 ORDER BY priority DESC, createdAt DESC")
    fun getPendingTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM life_tasks WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTasks(): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM life_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)

    @Query("DELETE FROM life_tasks WHERE isCompleted = 1")
    suspend fun deleteCompletedTasks(): Int
}

@Dao
interface LifeHabitDao {
    @Query("SELECT * FROM life_habits ORDER BY createdAt ASC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM life_habits WHERE isArchived = 0 ORDER BY createdAt ASC")
    fun getActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM life_habits WHERE id = :id LIMIT 1")
    fun getHabitById(id: String): Flow<HabitEntity?>

    @Query("SELECT * FROM life_habits WHERE id = :id LIMIT 1")
    suspend fun getHabitByIdDirect(id: String): HabitEntity?

    @Transaction
    @Query("SELECT * FROM life_habits WHERE isArchived = 0 ORDER BY createdAt ASC")
    fun getHabitsWithLogs(): Flow<List<HabitWithLogs>>

    @Transaction
    @Query("SELECT * FROM life_habits WHERE id = :id LIMIT 1")
    fun getHabitWithLogsById(id: String): Flow<HabitWithLogs?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Query("DELETE FROM life_habits WHERE id = :id")
    suspend fun deleteHabitById(id: String)

    @Query("UPDATE life_habits SET streak = :currentStreak, bestStreak = :bestStreak WHERE id = :habitId")
    suspend fun updateStreaks(habitId: String, currentStreak: Int, bestStreak: Int)

    @Query("UPDATE life_habits SET completedToday = :completed WHERE id = :habitId")
    suspend fun updateCompletedToday(habitId: String, completed: Boolean)
}

@Dao
interface LifeHabitLogDao {
    @Query("SELECT * FROM life_habit_logs WHERE habitId = :habitId ORDER BY completionDate DESC")
    fun getLogsForHabit(habitId: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM life_habit_logs WHERE completionDate = :date")
    fun getLogsForDate(date: String): Flow<List<HabitLogEntity>>

    @Query("SELECT * FROM life_habit_logs WHERE habitId = :habitId AND completionDate = :date LIMIT 1")
    suspend fun getLogForHabitAndDate(habitId: String, date: String): HabitLogEntity?

    @Query("SELECT * FROM life_habit_logs WHERE completionDate BETWEEN :startDate AND :endDate ORDER BY completionDate ASC")
    fun getLogsBetweenDates(startDate: String, endDate: String): Flow<List<HabitLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HabitLogEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<HabitLogEntity>)

    @Delete
    suspend fun deleteLog(log: HabitLogEntity)

    @Query("DELETE FROM life_habit_logs WHERE habitId = :habitId AND completionDate = :date")
    suspend fun deleteLogForHabitAndDate(habitId: String, date: String): Int

    @Query("SELECT COUNT(*) FROM life_habit_logs WHERE habitId = :habitId AND completed = 1")
    suspend fun getCompletedCountForHabit(habitId: String): Int

    @Query("SELECT COUNT(*) FROM life_habit_logs WHERE completionDate = :date AND completed = 1")
    fun countCompletedOnDate(date: String): Flow<Int>
}

@Dao
interface LifeProjectDao {
    @Query("SELECT * FROM life_projects")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)
}

@Dao
interface LifeGoalDao {
    @Query("SELECT * FROM life_goals")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: GoalEntity)

    @Update
    suspend fun updateGoal(goal: GoalEntity)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)
}

@Dao
interface LifeFocusDao {
    @Query("SELECT * FROM life_focus_sessions ORDER BY completedAt DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity)
}

@Dao
interface LifeFinanceDao {
    @Query("SELECT * FROM life_finance_records ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<FinanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinanceEntity)

    @Delete
    suspend fun deleteTransaction(transaction: FinanceEntity)
}

@Dao
interface LifeHealthDao {
    @Query("SELECT * FROM life_health_records ORDER BY timestamp DESC")
    fun getAllHealthRecords(): Flow<List<HealthEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthRecord(record: HealthEntity)
}

@Dao
interface LifeKnowledgeDao {
    @Query("SELECT * FROM life_knowledge_notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<KnowledgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: KnowledgeEntity)

    @Delete
    suspend fun deleteNote(note: KnowledgeEntity)
}

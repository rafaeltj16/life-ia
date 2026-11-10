package com.lifeos.features.habits.domain.repository

import com.lifeos.core.database.entity.HabitEntity
import com.lifeos.core.database.entity.HabitLogEntity
import com.lifeos.core.database.entity.HabitWithLogs
import com.lifeos.features.habits.domain.model.HabitDefinition
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for the Habit Tracker module.
 * Provides APIs to define daily habits, log/unlog completions, and observe reactive data from Room.
 */
interface HabitTrackerRepository {
    /**
     * Observes all active habits combined with their completion logs.
     */
    fun getHabitsWithLogs(): Flow<List<HabitWithLogs>>

    /**
     * Observes all active habits.
     */
    fun getActiveHabits(): Flow<List<HabitEntity>>

    /**
     * Observes a specific habit with logs.
     */
    fun getHabitWithLogsById(id: String): Flow<HabitWithLogs?>

    /**
     * Observes logs for a specific calendar date (formatted "YYYY-MM-DD").
     */
    fun getLogsForDate(date: String): Flow<List<HabitLogEntity>>

    /**
     * Defines a new daily habit in Room.
     */
    suspend fun defineHabit(definition: HabitDefinition): HabitEntity

    /**
     * Updates an existing habit definition.
     */
    suspend fun updateHabit(habit: HabitEntity)

    /**
     * Deletes a habit and all associated completion logs from Room.
     */
    suspend fun deleteHabit(habitId: String)

    /**
     * Logs a completion for a habit on a specific date.
     * Recalculates consecutive streak and best streak.
     */
    suspend fun logCompletion(
        habitId: String,
        date: String,
        value: Int = 1,
        notes: String = ""
    ): HabitLogEntity

    /**
     * Removes a logged completion for a habit on a specific date.
     * Recalculates streak.
     */
    suspend fun unlogCompletion(habitId: String, date: String): Boolean

    /**
     * Toggles completion for a habit on a given date (logs if absent, unlogs if present).
     * Returns true if now completed, false if uncompleted.
     */
    suspend fun toggleCompletion(habitId: String, date: String): Boolean

    /**
     * Seeds initial default habits and logs if the database is currently empty.
     */
    suspend fun seedDefaultHabitsIfEmpty()
}

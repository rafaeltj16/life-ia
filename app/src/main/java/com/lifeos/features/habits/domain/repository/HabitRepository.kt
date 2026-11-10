package com.lifeos.features.habits.domain.repository

import com.example.core.common.Habit
import com.example.core.common.HabitItem

interface HabitRepository {
    suspend fun getHabits(userId: String): List<HabitItem>
    suspend fun saveHabit(userId: String, habit: HabitItem)
    suspend fun deleteHabit(userId: String, habitId: String)

    suspend fun getTodayHabits(): List<Habit>
    suspend fun save(habit: Habit)
    suspend fun update(habit: Habit)
    suspend fun delete(id: String)
}

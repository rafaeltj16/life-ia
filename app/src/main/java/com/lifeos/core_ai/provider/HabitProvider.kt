package com.lifeos.core_ai.provider

import com.lifeos.habits.domain.model.Habit

interface HabitProvider {

    suspend fun getTodayHabits(): List<Habit>

    suspend fun getCompletedToday(): List<Habit>

    suspend fun getCurrentStreak(): Int

}

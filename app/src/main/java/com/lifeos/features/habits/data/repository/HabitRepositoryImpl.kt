package com.lifeos.features.habits.data.repository

import com.example.core.common.Habit
import com.example.core.common.HabitItem
import com.lifeos.features.habits.domain.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitRepositoryImpl : HabitRepository {
    private val localHabits = mutableMapOf<String, MutableList<HabitItem>>()

    private fun getDefaultHabits(): MutableList<HabitItem> {
        return mutableListOf(
            HabitItem(name = "Beber 2.5L de água", streak = 5, isCompletedToday = false, icon = "💧"),
            HabitItem(name = "Meditação Matinal", streak = 12, isCompletedToday = true, icon = "🧘"),
            HabitItem(name = "Leitura (15 páginas)", streak = 3, isCompletedToday = false, icon = "📚"),
            HabitItem(name = "Dormir 8 Horas", streak = 7, isCompletedToday = true, icon = "🌙")
        )
    }

    override suspend fun getHabits(userId: String): List<HabitItem> = withContext(Dispatchers.IO) {
        return@withContext localHabits.getOrPut(userId) { getDefaultHabits() }
    }

    override suspend fun saveHabit(userId: String, habit: HabitItem) {
        withContext(Dispatchers.IO) {
            val list = localHabits.getOrPut(userId) { getDefaultHabits() }
            val index = list.indexOfFirst { it.id == habit.id }
            if (index != -1) {
                list[index] = habit
            } else {
                list.add(habit)
            }
        }
    }

    override suspend fun deleteHabit(userId: String, habitId: String) = withContext(Dispatchers.IO) {
        val list = localHabits[userId]
        list?.removeAll { it.id == habitId }
        Unit
    }

    override suspend fun getTodayHabits(): List<Habit> {
        return getHabits("")
    }

    override suspend fun save(habit: Habit) {
        saveHabit("", habit)
    }

    override suspend fun update(habit: Habit) {
        saveHabit("", habit)
    }

    override suspend fun delete(id: String) {
        deleteHabit("", id)
    }
}

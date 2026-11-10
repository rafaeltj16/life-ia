package com.lifeos.features.habits.data.repository

import com.lifeos.core.database.dao.LifeHabitDao
import com.lifeos.core.database.dao.LifeHabitLogDao
import com.lifeos.core.database.entity.HabitEntity
import com.lifeos.core.database.entity.HabitLogEntity
import com.lifeos.core.database.entity.HabitWithLogs
import com.lifeos.features.habits.domain.model.HabitDefinition
import com.lifeos.features.habits.domain.repository.HabitTrackerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Room-backed implementation of [HabitTrackerRepository].
 * Handles database operations on [LifeHabitDao] and [LifeHabitLogDao]
 * with automatic streak recomputation.
 */
class RoomHabitTrackerRepositoryImpl(
    private val habitDao: LifeHabitDao,
    private val habitLogDao: LifeHabitLogDao
) : HabitTrackerRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun getHabitsWithLogs(): Flow<List<HabitWithLogs>> {
        return habitDao.getHabitsWithLogs()
    }

    override fun getActiveHabits(): Flow<List<HabitEntity>> {
        return habitDao.getActiveHabits()
    }

    override fun getHabitWithLogsById(id: String): Flow<HabitWithLogs?> {
        return habitDao.getHabitWithLogsById(id)
    }

    override fun getLogsForDate(date: String): Flow<List<HabitLogEntity>> {
        return habitLogDao.getLogsForDate(date)
    }

    override suspend fun defineHabit(definition: HabitDefinition): HabitEntity = withContext(Dispatchers.IO) {
        val entity = HabitEntity(
            id = definition.id,
            name = definition.name,
            description = definition.description,
            streak = 0,
            bestStreak = 0,
            targetDays = definition.targetDays,
            completedToday = false,
            category = definition.category,
            iconName = definition.iconName,
            colorHex = definition.colorHex,
            frequency = definition.frequency,
            targetValue = definition.targetValue,
            unit = definition.unit,
            reminderTime = definition.reminderTime,
            isArchived = false,
            createdAt = System.currentTimeMillis()
        )
        habitDao.insertHabit(entity)
        entity
    }

    override suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    override suspend fun deleteHabit(habitId: String) = withContext(Dispatchers.IO) {
        habitDao.deleteHabitById(habitId)
    }

    override suspend fun logCompletion(
        habitId: String,
        date: String,
        value: Int,
        notes: String
    ): HabitLogEntity = withContext(Dispatchers.IO) {
        val existingLog = habitLogDao.getLogForHabitAndDate(habitId, date)
        val log = if (existingLog != null) {
            existingLog.copy(
                completed = true,
                valueLogged = value,
                notes = notes,
                loggedAt = System.currentTimeMillis()
            )
        } else {
            HabitLogEntity(
                id = UUID.randomUUID().toString(),
                habitId = habitId,
                completionDate = date,
                completed = true,
                valueLogged = value,
                notes = notes,
                loggedAt = System.currentTimeMillis()
            )
        }
        habitLogDao.insertLog(log)
        recomputeHabitStreaks(habitId)
        log
    }

    override suspend fun unlogCompletion(habitId: String, date: String): Boolean = withContext(Dispatchers.IO) {
        val deletedCount = habitLogDao.deleteLogForHabitAndDate(habitId, date)
        if (deletedCount > 0) {
            recomputeHabitStreaks(habitId)
            true
        } else {
            false
        }
    }

    override suspend fun toggleCompletion(habitId: String, date: String): Boolean = withContext(Dispatchers.IO) {
        val existingLog = habitLogDao.getLogForHabitAndDate(habitId, date)
        if (existingLog != null && existingLog.completed) {
            habitLogDao.deleteLog(existingLog)
            recomputeHabitStreaks(habitId)
            false
        } else {
            val log = HabitLogEntity(
                id = existingLog?.id ?: UUID.randomUUID().toString(),
                habitId = habitId,
                completionDate = date,
                completed = true,
                valueLogged = 1,
                notes = "",
                loggedAt = System.currentTimeMillis()
            )
            habitLogDao.insertLog(log)
            recomputeHabitStreaks(habitId)
            true
        }
    }

    private suspend fun recomputeHabitStreaks(habitId: String) {
        try {
            val habit = habitDao.getHabitByIdDirect(habitId) ?: return
            val logs = habitLogDao.getLogsForHabit(habitId).first()
            val completedDates = logs
                .filter { it.completed }
                .mapNotNull {
                    try {
                        LocalDate.parse(it.completionDate, dateFormatter)
                    } catch (e: Exception) {
                        null
                    }
                }
                .toSet()

            val today = LocalDate.now()
            val isCompletedToday = completedDates.contains(today)

            // Calculate current streak
            var currentStreak = 0
            var checkDate = if (isCompletedToday) today else today.minusDays(1)

            while (completedDates.contains(checkDate)) {
                currentStreak++
                checkDate = checkDate.minusDays(1)
            }

            // Calculate best streak
            val sortedDates = completedDates.sorted()
            var bestStreak = 0
            var runningStreak = 0
            var prevDate: LocalDate? = null

            for (date in sortedDates) {
                if (prevDate == null || date == prevDate.plusDays(1)) {
                    runningStreak++
                } else if (date != prevDate) {
                    runningStreak = 1
                }
                if (runningStreak > bestStreak) {
                    bestStreak = runningStreak
                }
                prevDate = date
            }

            if (currentStreak > bestStreak) {
                bestStreak = currentStreak
            }

            habitDao.updateStreaks(habitId, currentStreak, maxOf(bestStreak, habit.bestStreak))
            habitDao.updateCompletedToday(habitId, isCompletedToday)
        } catch (e: Exception) {
            Timber.e(e, "RoomHabitTrackerRepository: Failed to recompute streak for $habitId")
        }
    }

    override suspend fun seedDefaultHabitsIfEmpty() = withContext(Dispatchers.IO) {
        val existing = habitDao.getAllHabits().first()
        if (existing.isNotEmpty()) return@withContext

        val today = LocalDate.now()
        val defaultHabits = listOf(
            HabitDefinition(
                id = "habit_water",
                name = "Beber 2.5L de água",
                description = "Manter hidratação regular ao longo de todo o dia",
                category = "HEALTH",
                iconName = "💧",
                colorHex = "#0288D1",
                targetDays = 7,
                targetValue = 2500,
                unit = "ml"
            ),
            HabitDefinition(
                id = "habit_meditation",
                name = "Meditação Matinal",
                description = "10 minutos de respiração consciente e foco pleno",
                category = "MINDFULNESS",
                iconName = "🧘",
                colorHex = "#7B1FA2",
                targetDays = 7,
                targetValue = 10,
                unit = "minutos"
            ),
            HabitDefinition(
                id = "habit_reading",
                name = "Leitura de Foco",
                description = "Leitura técnica ou desenvolvimento pessoal (15 páginas)",
                category = "PRODUCTIVITY",
                iconName = "📚",
                colorHex = "#F57C00",
                targetDays = 5,
                targetValue = 15,
                unit = "páginas"
            ),
            HabitDefinition(
                id = "habit_workout",
                name = "Treino / Exercício Físico",
                description = "Musculação, corrida ou treino funcional de 45 min",
                category = "HEALTH",
                iconName = "🏋️",
                colorHex = "#E53935",
                targetDays = 5,
                targetValue = 45,
                unit = "minutos"
            ),
            HabitDefinition(
                id = "habit_sleep",
                name = "Dormir 8 Horas",
                description = "Higiene do sono para recuperação celular e cognitiva",
                category = "HEALTH",
                iconName = "🌙",
                colorHex = "#3949AB",
                targetDays = 7,
                targetValue = 8,
                unit = "horas"
            )
        )

        val insertedEntities = mutableListOf<HabitEntity>()
        for (def in defaultHabits) {
            val entity = HabitEntity(
                id = def.id,
                name = def.name,
                description = def.description,
                streak = 3,
                bestStreak = 14,
                targetDays = def.targetDays,
                completedToday = false,
                category = def.category,
                iconName = def.iconName,
                colorHex = def.colorHex,
                frequency = def.frequency,
                targetValue = def.targetValue,
                unit = def.unit,
                reminderTime = def.reminderTime,
                isArchived = false,
                createdAt = System.currentTimeMillis() - 7 * 86400000L
            )
            insertedEntities.add(entity)
        }
        habitDao.insertHabits(insertedEntities)

        // Seed logs for the past 4 days
        val sampleLogs = mutableListOf<HabitLogEntity>()
        for (dayOffset in 1..4) {
            val logDate = today.minusDays(dayOffset.toLong()).format(dateFormatter)
            sampleLogs.add(
                HabitLogEntity(
                    id = UUID.randomUUID().toString(),
                    habitId = "habit_water",
                    completionDate = logDate,
                    completed = true,
                    valueLogged = 2500
                )
            )
            sampleLogs.add(
                HabitLogEntity(
                    id = UUID.randomUUID().toString(),
                    habitId = "habit_meditation",
                    completionDate = logDate,
                    completed = true,
                    valueLogged = 10
                )
            )
            if (dayOffset <= 3) {
                sampleLogs.add(
                    HabitLogEntity(
                        id = UUID.randomUUID().toString(),
                        habitId = "habit_reading",
                        completionDate = logDate,
                        completed = true,
                        valueLogged = 15
                    )
                )
            }
        }
        habitLogDao.insertLogs(sampleLogs)

        // Recompute streaks for all seeded habits
        for (def in defaultHabits) {
            recomputeHabitStreaks(def.id)
        }
    }
}

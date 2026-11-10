package com.lifeos.features.habits

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.lifeos.core.database.LifeOSDatabase
import com.lifeos.features.habits.data.repository.RoomHabitTrackerRepositoryImpl
import com.lifeos.features.habits.domain.model.HabitDefinition
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HabitTrackerTest {

    private lateinit var database: LifeOSDatabase
    private lateinit var repository: RoomHabitTrackerRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LifeOSDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RoomHabitTrackerRepositoryImpl(
            habitDao = database.habitDao(),
            habitLogDao = database.habitLogDao()
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testRoomHabitDefinitionAndCompletionLogging() = runBlocking {
        val definition = HabitDefinition(
            name = "Leitura Diária",
            description = "Ler 20 páginas de um livro",
            category = "PRODUCTIVITY",
            iconName = "📚",
            targetDays = 7,
            targetValue = 20,
            unit = "páginas"
        )

        // 1. Define habit in Room
        val created = repository.defineHabit(definition)
        assertEquals("Leitura Diária", created.name)
        assertEquals(7, created.targetDays)
        assertEquals(0, created.streak)

        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        // 2. Log completion for today
        val log = repository.logCompletion(
            habitId = created.id,
            date = today,
            value = 20,
            notes = "Capítulo 1 e 2 concluídos"
        )
        assertNotNull(log)
        assertEquals(created.id, log.habitId)
        assertTrue(log.completed)

        // 3. Verify Room relation query (HabitWithLogs) reflects streak and log
        val habitsWithLogs = repository.getHabitsWithLogs().first()
        assertEquals(1, habitsWithLogs.size)
        val habitWithLogs = habitsWithLogs.first()
        assertEquals(1, habitWithLogs.habit.streak)
        assertTrue(habitWithLogs.habit.completedToday)
        assertEquals(1, habitWithLogs.logs.size)

        // 4. Toggle completion off
        val isNowCompleted = repository.toggleCompletion(created.id, today)
        assertFalse(isNowCompleted)

        val updatedList = repository.getHabitsWithLogs().first()
        assertEquals(0, updatedList.first().habit.streak)
        assertFalse(updatedList.first().habit.completedToday)
    }

    @Test
    fun testMultipleDaysConsecutiveStreak() = runBlocking {
        val definition = HabitDefinition(
            name = "Hidratação",
            description = "2.5L de água",
            category = "HEALTH",
            iconName = "💧"
        )
        val created = repository.defineHabit(definition)

        val today = LocalDate.now()
        val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

        // Log completions for today and the previous 2 days
        repository.logCompletion(created.id, today.minusDays(2).format(dateFormatter))
        repository.logCompletion(created.id, today.minusDays(1).format(dateFormatter))
        repository.logCompletion(created.id, today.format(dateFormatter))

        val habitsWithLogs = repository.getHabitsWithLogs().first()
        val habitWithLogs = habitsWithLogs.first { it.habit.id == created.id }
        assertEquals(3, habitWithLogs.habit.streak)
        assertEquals(3, habitWithLogs.habit.bestStreak)
        assertTrue(habitWithLogs.habit.completedToday)
    }
}

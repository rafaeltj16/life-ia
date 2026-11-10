package com.example.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TaskRepositoryTest {

    private lateinit var database: AppDatabase
    private lateinit var taskRepository: TaskRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskRepository = TaskRepository(database.taskDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCreateTaskAndObserveReactiveFlows() = runBlocking {
        // Create tasks using repository clean API
        val task1 = taskRepository.createTask(
            title = "Arquitetura do App",
            description = "Definir padrões de Repository e ViewModel",
            priority = "HIGH",
            dueDate = "Hoje",
            category = "Engenharia"
        )
        val task2 = taskRepository.createTask(
            title = "Configurar CI/CD",
            description = "Automatizar builds e testes unitários",
            priority = "MEDIUM",
            dueDate = "Amanhã",
            category = "DevOps"
        )

        // Observe allTasks
        val all = taskRepository.allTasks.first()
        assertEquals(2, all.size)

        // Observe pending count
        val pendingCount = taskRepository.pendingTasksCount.first()
        assertEquals(2, pendingCount)

        // Toggle completion on task1
        val isCompleted = taskRepository.toggleTaskCompletion(task1.id)
        assertEquals(true, isCompleted)

        // Observe completed tasks
        val completed = taskRepository.completedTasks.first()
        assertEquals(1, completed.size)
        assertEquals(task1.id, completed.first().id)

        // Observe pending tasks
        val pending = taskRepository.pendingTasks.first()
        assertEquals(1, pending.size)
        assertEquals(task2.id, pending.first().id)
    }

    @Test
    fun testTaskFilteringByPriorityAndDueDate() = runBlocking {
        taskRepository.createTask(
            title = "Tarefa Urgente",
            priority = "HIGH",
            dueDate = "2026-11-08"
        )
        taskRepository.createTask(
            title = "Tarefa Moderada",
            priority = "MEDIUM",
            dueDate = "2026-11-09"
        )

        val highPriority = taskRepository.getTasksByPriority("HIGH").first()
        assertEquals(1, highPriority.size)
        assertEquals("Tarefa Urgente", highPriority.first().title)

        val dueToday = taskRepository.getTasksByDueDate("2026-11-08").first()
        assertEquals(1, dueToday.size)
        assertEquals("Tarefa Urgente", dueToday.first().title)
    }

    @Test
    fun testDeleteOperations() = runBlocking {
        val t1 = taskRepository.createTask(title = "T1", dueDate = "Hoje")
        val t2 = taskRepository.createTask(title = "T2", dueDate = "Hoje")

        assertEquals(2, taskRepository.allTasks.first().size)

        // Delete by ID
        taskRepository.deleteById(t1.id)
        assertEquals(1, taskRepository.allTasks.first().size)

        // Complete remaining task and delete completed
        taskRepository.updateTaskCompletion(t2.id, true)
        val deletedCount = taskRepository.deleteCompletedTasks()
        assertEquals(1, deletedCount)
        assertEquals(0, taskRepository.allTasks.first().size)
    }
}

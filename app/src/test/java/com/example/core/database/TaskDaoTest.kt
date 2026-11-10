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
class TaskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        taskDao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testTaskEntityAndInsertion() = runBlocking {
        val task = TaskEntity(
            id = "task_001",
            title = "Finalizar Relatório Trimestral",
            description = "Consolidar métricas de vendas e KPIs de produtividade para apresentação.",
            priority = "HIGH",
            dueDate = "2026-11-15",
            category = "Trabalho",
            isCompleted = false
        )

        // Insertion
        taskDao.insertTask(task)

        // Query by ID
        val retrieved = taskDao.getTaskById("task_001").first()
        assertNotNull(retrieved)
        assertEquals("Finalizar Relatório Trimestral", retrieved?.title)
        assertEquals("Consolidar métricas de vendas e KPIs de produtividade para apresentação.", retrieved?.description)
        assertEquals("HIGH", retrieved?.priority)
        assertEquals("2026-11-15", retrieved?.dueDate)
        assertFalse(retrieved?.isCompleted ?: true)
    }

    @Test
    fun testTaskQueriesFilteringAndSorting() = runBlocking {
        val task1 = TaskEntity(
            id = "t1",
            title = "Pagar conta de luz",
            description = "Via Pix antes das 18h",
            priority = "MEDIUM",
            dueDate = "2026-11-09",
            isCompleted = false
        )
        val task2 = TaskEntity(
            id = "t2",
            title = "Revisar PR crítico",
            description = "Code review de arquitetura Room",
            priority = "HIGH",
            dueDate = "2026-11-08",
            isCompleted = false
        )
        val task3 = TaskEntity(
            id = "t3",
            title = "Comprar café em grãos",
            description = "Torra média",
            priority = "LOW",
            dueDate = "2026-11-10",
            isCompleted = true
        )

        taskDao.insertTasks(listOf(task1, task2, task3))

        // Query pending tasks
        val pendingTasks = taskDao.getPendingTasks().first()
        assertEquals(2, pendingTasks.size)
        // High priority first
        assertEquals("t2", pendingTasks[0].id)
        assertEquals("t1", pendingTasks[1].id)

        // Query completed tasks
        val completedTasks = taskDao.getCompletedTasks().first()
        assertEquals(1, completedTasks.size)
        assertEquals("t3", completedTasks[0].id)

        // Query by priority
        val highPriorityTasks = taskDao.getTasksByPriority("HIGH").first()
        assertEquals(1, highPriorityTasks.size)
        assertEquals("Revisar PR crítico", highPriorityTasks[0].title)

        // Query by dueDate
        val tasksDueToday = taskDao.getTasksByDueDate("2026-11-08").first()
        assertEquals(1, tasksDueToday.size)
        assertEquals("t2", tasksDueToday[0].id)
    }

    @Test
    fun testTaskDeletionAndCompletionUpdate() = runBlocking {
        val task = TaskEntity(
            id = "task_del",
            title = "Tarefa Temporária",
            description = "Será deletada após teste",
            priority = "LOW",
            dueDate = "Hoje",
            isCompleted = false
        )

        taskDao.insertTask(task)
        assertNotNull(taskDao.getTaskById("task_del").first())

        // Update completion
        taskDao.updateTaskCompletion("task_del", true)
        val updated = taskDao.getTaskById("task_del").first()
        assertTrue(updated?.isCompleted ?: false)
        assertEquals("COMPLETED", updated?.status)

        // Deletion by entity
        taskDao.deleteTask(updated!!)
        assertNull(taskDao.getTaskById("task_del").first())

        // Insertion and deletion by ID
        taskDao.insertTask(task)
        taskDao.deleteTaskById("task_del")
        assertNull(taskDao.getTaskById("task_del").first())
    }
}

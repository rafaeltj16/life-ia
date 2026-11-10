package com.lifeos.features.tasks

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.database.AppDatabase
import com.example.core.database.TaskRepository
import com.lifeos.features.tasks.presentation.viewmodel.TaskFilter
import com.lifeos.features.tasks.presentation.viewmodel.TaskSortOption
import com.lifeos.features.tasks.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TaskViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var database: AppDatabase
    private lateinit var repository: TaskRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TaskRepository(database.taskDao())
        viewModel = TaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        database.close()
    }

    @Test
    fun testAddTaskAndStateUpdate() = runTest {
        viewModel.addTask(
            title = "Apresentação da Diretoria",
            description = "Preparar slides de KPIs e roadmap",
            priority = "HIGH",
            dueDate = "Hoje",
            category = "Trabalho"
        )

        val state = viewModel.uiState.first { it.tasks.any { t -> t.title == "Apresentação da Diretoria" } }
        assertEquals(1, state.totalCount)
        assertEquals(1, state.pendingCount)
        assertEquals(0, state.completedCount)
        assertEquals(0, state.completionPercentage)

        val task = state.tasks.first()
        assertEquals("Apresentação da Diretoria", task.title)
        assertEquals("Preparar slides de KPIs e roadmap", task.description)
        assertEquals("HIGH", task.priority)
        assertFalse(task.isCompleted)
    }

    @Test
    fun testToggleCompletionAndFilter() = runTest {
        viewModel.addTask(title = "Tarefa 1", priority = "HIGH", dueDate = "Hoje")
        viewModel.addTask(title = "Tarefa 2", priority = "LOW", dueDate = "Amanhã")

        val stateAfterAdd = viewModel.uiState.first { it.tasks.size == 2 }
        val task1 = stateAfterAdd.tasks.first { it.title == "Tarefa 1" }

        // Toggle task 1 completion
        viewModel.toggleTaskCompletion(task1.id)

        val stateAfterToggle = viewModel.uiState.first { it.completedCount == 1 }
        assertEquals(1, stateAfterToggle.pendingCount)
        assertEquals(1, stateAfterToggle.completedCount)
        assertEquals(50, stateAfterToggle.completionPercentage)

        // Filter by Pending
        viewModel.setFilter(TaskFilter.PENDING)
        val pendingState = viewModel.uiState.first { it.selectedFilter == TaskFilter.PENDING && it.tasks.size == 1 }
        assertEquals("Tarefa 2", pendingState.tasks.first().title)

        // Filter by Completed
        viewModel.setFilter(TaskFilter.COMPLETED)
        val completedState = viewModel.uiState.first { it.selectedFilter == TaskFilter.COMPLETED && it.tasks.size == 1 }
        assertEquals("Tarefa 1", completedState.tasks.first().title)
    }

    @Test
    fun testSearchAndSort() = runTest {
        viewModel.addTask(title = "Comprar café", category = "Pessoal", priority = "LOW")
        viewModel.addTask(title = "Revisar relatório", category = "Trabalho", priority = "HIGH")

        val state = viewModel.uiState.first { it.tasks.size == 2 }

        // Search query
        viewModel.setSearchQuery("café")
        val searchState = viewModel.uiState.first { it.searchQuery == "café" && it.tasks.size == 1 }
        assertEquals("Comprar café", searchState.tasks.first().title)

        // Reset search
        viewModel.setSearchQuery("")
        val resetSearchState = viewModel.uiState.first { it.searchQuery == "" && it.tasks.size == 2 }

        // Sort by Priority Descending (HIGH before LOW)
        viewModel.setSortOption(TaskSortOption.PRIORITY_DESC)
        val sortState = viewModel.uiState.first { it.sortOption == TaskSortOption.PRIORITY_DESC }
        assertEquals("Revisar relatório", sortState.tasks[0].title)
        assertEquals("Comprar café", sortState.tasks[1].title)
    }

    @Test
    fun testDeleteOperations() = runTest {
        viewModel.addTask(title = "Task To Delete", priority = "MEDIUM")
        val state = viewModel.uiState.first { it.tasks.size == 1 }
        val task = state.tasks.first()

        viewModel.deleteTask(task)
        val emptyState = viewModel.uiState.first { it.tasks.isEmpty() }
        assertEquals(0, emptyState.totalCount)
    }

    @Test
    fun testSearchByCategoryInRealTime() = runTest {
        viewModel.addTask(title = "Consulta médica", category = "Saúde", priority = "HIGH")
        viewModel.addTask(title = "Preparar apresentação", category = "Trabalho", priority = "MEDIUM")
        viewModel.addTask(title = "Estudar Jetpack Compose", category = "Estudos", priority = "LOW")

        viewModel.uiState.first { it.tasks.size == 3 }

        // Search by category "Saúde"
        viewModel.setSearchQuery("Saúde")
        val healthSearch = viewModel.uiState.first { it.searchQuery == "Saúde" && it.tasks.size == 1 }
        assertEquals("Consulta médica", healthSearch.tasks.first().title)
        assertEquals("Saúde", healthSearch.tasks.first().category)

        // Search by category "Trabalho"
        viewModel.setSearchQuery("Trabalho")
        val workSearch = viewModel.uiState.first { it.searchQuery == "Trabalho" && it.tasks.size == 1 }
        assertEquals("Preparar apresentação", workSearch.tasks.first().title)
        assertEquals("Trabalho", workSearch.tasks.first().category)

        // Search by category key in English (e.g. "Work")
        viewModel.setSearchQuery("Work")
        val workKeySearch = viewModel.uiState.first { it.searchQuery == "Work" && it.tasks.size == 1 }
        assertEquals("Preparar apresentação", workKeySearch.tasks.first().title)
    }
}

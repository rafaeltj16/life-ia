package com.lifeos.features.tasks

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.database.AppDatabase
import com.example.core.database.TaskEntity
import com.example.core.database.TaskRepository
import com.lifeos.features.tasks.presentation.screen.TaskListScreen
import com.lifeos.features.tasks.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TaskListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

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
    fun testTaskListScreenRendersAndToggles() {
        runBlocking {
            repository.insertTask(
                TaskEntity(
                    id = "test_task_1",
                    title = "Comprar insumos",
                    description = "Café e água",
                    priority = "HIGH",
                    dueDate = "Hoje"
                )
            )
        }

        composeTestRule.setContent {
            TaskListScreen(viewModel = viewModel)
        }

        composeTestRule.waitUntil(5000) {
            viewModel.uiState.value.tasks.any { it.id == "test_task_1" }
        }
        composeTestRule.waitForIdle()

        // Verify task title is rendered in LazyColumn
        composeTestRule.onNodeWithText("Comprar insumos").assertExists()

        // Toggle task completion via Checkbox
        composeTestRule.onNodeWithTag("task_checkbox_test_task_1").performClick()
        composeTestRule.waitForIdle()

        // Verify progress percentage updates
        composeTestRule.onNodeWithTag("task_progress_percentage_text").assertExists()

        // Verify category badge is rendered on the task item
        composeTestRule.onNodeWithTag("task_category_badge_test_task_1").assertExists()
    }

    @Test
    fun testSearchBarFiltersByTitleInRealTime() {
        runBlocking {
            repository.insertTask(
                TaskEntity(
                    id = "task_alpha",
                    title = "Comprar suplementos",
                    category = "Saúde"
                )
            )
            repository.insertTask(
                TaskEntity(
                    id = "task_beta",
                    title = "Relatório financeiro Q4",
                    category = "Trabalho"
                )
            )
        }

        composeTestRule.setContent {
            TaskListScreen(viewModel = viewModel)
        }

        composeTestRule.waitUntil(5000) {
            viewModel.uiState.value.tasks.size == 2
        }
        composeTestRule.waitForIdle()

        // Search by title in real-time
        composeTestRule.onNodeWithTag("task_search_field").performTextInput("Relatório")
        composeTestRule.waitForIdle()

        // Verify only matching task is shown
        composeTestRule.onNodeWithText("Relatório financeiro Q4").assertExists()
        composeTestRule.onNodeWithText("Comprar suplementos").assertDoesNotExist()

        // Clear search and verify both reappear
        composeTestRule.onNodeWithTag("clear_search_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Relatório financeiro Q4").assertExists()
        composeTestRule.onNodeWithText("Comprar suplementos").assertExists()
    }

    @Test
    fun testSearchBarFiltersByCategoryInRealTime() {
        runBlocking {
            repository.insertTask(
                TaskEntity(
                    id = "task_gym",
                    title = "Treino de perna",
                    category = "Saúde"
                )
            )
            repository.insertTask(
                TaskEntity(
                    id = "task_presentation",
                    title = "Apresentar pitch",
                    category = "Trabalho"
                )
            )
        }

        composeTestRule.setContent {
            TaskListScreen(viewModel = viewModel)
        }

        composeTestRule.waitUntil(5000) {
            viewModel.uiState.value.tasks.size >= 2
        }
        composeTestRule.waitForIdle()

        // Filter by category name in search bar
        composeTestRule.onNodeWithTag("task_search_field").performTextInput("Saúde")
        composeTestRule.waitForIdle()

        // Only health task should be visible
        composeTestRule.onNodeWithText("Treino de perna").assertExists()
        composeTestRule.onNodeWithText("Apresentar pitch").assertDoesNotExist()
    }
}

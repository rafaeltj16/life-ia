package com.lifeos.features.tasks

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.core.database.AppDatabase
import com.example.core.database.TaskRepository
import com.lifeos.features.tasks.presentation.components.AddTaskDialog
import com.lifeos.features.tasks.presentation.components.AddTaskFloatingActionButton
import com.lifeos.features.tasks.presentation.components.AddTaskSheetContent
import com.lifeos.features.tasks.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AddTaskComponentTest {

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
    fun testAddTaskFloatingActionButtonClicks() {
        var clicked = false
        composeTestRule.setContent {
            AddTaskFloatingActionButton(
                onClick = { clicked = true },
                text = "Nova Tarefa"
            )
        }

        // Verify FAB exists and triggers click
        composeTestRule.onNodeWithTag("add_task_fab")
            .assertExists()
            .performClick()

        assertTrue("FAB onClick should have been triggered", clicked)
    }

    @Test
    fun testAddTaskDialogSavesToViewModel() {
        var dismissed = false
        composeTestRule.setContent {
            AddTaskDialog(
                viewModel = viewModel,
                onDismiss = { dismissed = true }
            )
        }

        // Enter Title and Description
        composeTestRule.onNodeWithTag("input_task_title").performTextInput("Finalizar Apresentação")
        composeTestRule.onNodeWithTag("input_task_description").performTextInput("Adicionar gráficos de métricas")

        // Confirm / Save
        composeTestRule.onNodeWithTag("confirm_add_task_button").performClick()
        composeTestRule.waitForIdle()

        // Verify task was added to Room repository
        val tasks = runBlocking { repository.allTasks.first { list -> list.any { it.title == "Finalizar Apresentação" } } }
        val task = tasks.first { it.title == "Finalizar Apresentação" }
        assertEquals("Finalizar Apresentação", task.title)
        assertEquals("Adicionar gráficos de métricas", task.description)
        assertFalse(task.isCompleted)
        assertTrue("Dialog should be dismissed after saving", dismissed)
    }

    @Test
    fun testAddTaskSheetContentSavesToViewModel() {
        var dismissed = false
        composeTestRule.setContent {
            AddTaskSheetContent(
                onDismiss = { dismissed = true },
                onSaveTask = { title, description, priority, dueDate, category ->
                    viewModel.addTask(
                        title = title,
                        description = description,
                        priority = priority,
                        dueDate = dueDate,
                        category = category
                    )
                    dismissed = true
                }
            )
        }

        // Enter Title and Description
        composeTestRule.onNodeWithTag("input_task_title").performTextInput("Comprar Livro Kotlin")
        composeTestRule.onNodeWithTag("input_task_description").performTextInput("Edição atualizada 2026")

        // Click save button
        composeTestRule.onNodeWithTag("confirm_add_task_button").performClick()
        composeTestRule.waitForIdle()

        // Verify task is persisted in Room repository
        val tasks = runBlocking { repository.allTasks.first { list -> list.any { it.title == "Comprar Livro Kotlin" } } }
        val task = tasks.first { it.title == "Comprar Livro Kotlin" }
        assertEquals("Comprar Livro Kotlin", task.title)
        assertEquals("Edição atualizada 2026", task.description)
        assertTrue("Dismiss should be invoked", dismissed)
    }

    @Test
    fun testValidationPreventsEmptyTitleSave() {
        var saveCalled = false
        composeTestRule.setContent {
            AddTaskDialog(
                onDismiss = {},
                onSaveTask = { _, _, _, _, _ -> saveCalled = true }
            )
        }

        // Click confirm with blank title
        composeTestRule.onNodeWithTag("confirm_add_task_button").performClick()
        composeTestRule.waitForIdle()

        assertFalse("Save should not be invoked when title is empty", saveCalled)
    }

    @Test
    fun testAddTaskDialogCategoryDropdownSelection() {
        var savedCategory = ""
        composeTestRule.setContent {
            AddTaskDialog(
                onDismiss = {},
                onSaveTask = { _, _, _, _, cat -> savedCategory = cat }
            )
        }

        // Enter Title
        composeTestRule.onNodeWithTag("input_task_title").performTextInput("Consulta com Cardiologista")

        // Open Category Dropdown
        composeTestRule.onNodeWithTag("category_dropdown_selector").performClick()
        composeTestRule.waitForIdle()

        // Select 'HEALTH' (Saúde) option
        composeTestRule.onNodeWithTag("category_option_HEALTH").performClick()
        composeTestRule.waitForIdle()

        // Save
        composeTestRule.onNodeWithTag("confirm_add_task_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals("Saúde", savedCategory)
    }
}

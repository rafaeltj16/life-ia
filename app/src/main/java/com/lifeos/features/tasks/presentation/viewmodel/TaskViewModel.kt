package com.lifeos.features.tasks.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.database.TaskEntity
import com.example.core.database.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Filter options for task list display in UI.
 */
enum class TaskFilter(val displayName: String) {
    ALL("Todas"),
    PENDING("Pendentes"),
    COMPLETED("Concluídas"),
    TODAY("Para Hoje"),
    HIGH_PRIORITY("Prioridade Alta")
}

/**
 * Sorting options for tasks.
 */
enum class TaskSortOption(val displayName: String) {
    PRIORITY_DESC("Prioridade (Alta -> Baixa)"),
    DUE_DATE_ASC("Data de Vencimento"),
    CREATED_AT_DESC("Mais Recentes"),
    TITLE_ASC("Alfabética (A-Z)")
}

/**
 * UI State representing the current snapshot of tasks, active filters, and metrics.
 */
data class TaskUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val allTasks: List<TaskEntity> = emptyList(),
    val totalCount: Int = 0,
    val pendingCount: Int = 0,
    val completedCount: Int = 0,
    val completionPercentage: Int = 0,
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val selectedCategory: String = "Todos",
    val availableCategories: List<String> = listOf("Todos", "Pessoal", "Trabalho", "Saúde", "Estudos", "Finanças", "Geral"),
    val searchQuery: String = "",
    val sortOption: TaskSortOption = TaskSortOption.PRIORITY_DESC,
    val isLoading: Boolean = false,
    val userMessage: String? = null
)

/**
 * ViewModel that leverages [TaskRepository] to manage the UI state of tasks
 * and provides methods for the UI to observe and modify the task list.
 */
class TaskViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    private val _selectedCategory = MutableStateFlow("Todos")
    private val _searchQuery = MutableStateFlow("")
    private val _sortOption = MutableStateFlow(TaskSortOption.PRIORITY_DESC)
    private val _isLoading = MutableStateFlow(false)
    private val _userMessage = MutableStateFlow<String?>(null)

    private data class FilterParams(
        val filter: TaskFilter,
        val category: String,
        val query: String,
        val sortOption: TaskSortOption
    )

    private val _filterParams = combine(
        _selectedFilter,
        _selectedCategory,
        _searchQuery,
        _sortOption
    ) { filter, category, query, sortOption ->
        FilterParams(filter, category, query, sortOption)
    }

    val uiState: StateFlow<TaskUiState> = combine(
        repository.allTasks,
        _filterParams,
        _isLoading,
        _userMessage
    ) { allTasks, params, isLoading, message ->
        buildUiState(
            allTasks = allTasks,
            filter = params.filter,
            category = params.category,
            query = params.query,
            sortOption = params.sortOption,
            isLoading = isLoading,
            message = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskUiState(isLoading = true)
    )

    /**
     * Creates and persists a new task in the local Room database.
     */
    fun addTask(
        title: String,
        description: String = "",
        priority: String = "MEDIUM",
        dueDate: String = "Hoje",
        category: String = "Geral",
        tags: String = "",
        projectId: String? = null
    ) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            _userMessage.value = "O título da tarefa não pode estar vazio."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.createTask(
                    title = trimmedTitle,
                    description = description.trim(),
                    priority = priority.uppercase(),
                    dueDate = dueDate.trim(),
                    category = category.trim(),
                    tags = tags.trim(),
                    projectId = projectId
                )
                _userMessage.value = "Tarefa criada com sucesso!"
            } catch (e: Exception) {
                Timber.e(e, "TaskViewModel: Failed to add task")
                _userMessage.value = "Erro ao adicionar tarefa: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Updates an existing task entity.
     */
    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                repository.updateTask(task)
                _userMessage.value = "Tarefa atualizada."
            } catch (e: Exception) {
                Timber.e(e, "TaskViewModel: Failed to update task")
                _userMessage.value = "Erro ao atualizar tarefa."
            }
        }
    }

    /**
     * Toggles the completion status of a task by ID.
     */
    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            try {
                val newStatus = repository.toggleTaskCompletion(taskId)
                if (newStatus == true) {
                    _userMessage.value = "Tarefa concluída! Parabéns!"
                }
            } catch (e: Exception) {
                Timber.e(e, "TaskViewModel: Failed to toggle completion")
                _userMessage.value = "Erro ao alterar status da tarefa."
            }
        }
    }

    /**
     * Sets explicit completion status for a task by ID.
     */
    fun updateTaskCompletion(taskId: String, isCompleted: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateTaskCompletion(taskId, isCompleted)
            } catch (e: Exception) {
                Timber.e(e, "TaskViewModel: Failed to update task completion")
            }
        }
    }

    /**
     * Deletes a task by entity reference.
     */
    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
                _userMessage.value = "Tarefa removida."
            } catch (e: Exception) {
                Timber.e(e, "TaskViewModel: Failed to delete task")
                _userMessage.value = "Erro ao remover tarefa."
            }
        }
    }

    /**
     * Deletes a task by its unique ID.
     */
    fun deleteTaskById(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTaskById(taskId)
                _userMessage.value = "Tarefa removida."
            } catch (e: Exception) {
                Timber.e(e, "TaskViewModel: Failed to delete task by id")
                _userMessage.value = "Erro ao remover tarefa."
            }
        }
    }

    /**
     * Deletes all completed tasks from the database.
     */
    fun deleteCompletedTasks() {
        viewModelScope.launch {
            try {
                val count = repository.deleteCompletedTasks()
                _userMessage.value = if (count > 0) "$count tarefas concluídas removidas." else "Nenhuma tarefa concluída para remover."
            } catch (e: Exception) {
                Timber.e(e, "TaskViewModel: Failed to delete completed tasks")
                _userMessage.value = "Erro ao limpar tarefas concluídas."
            }
        }
    }

    /**
     * Clears all tasks from the local database.
     */
    fun clearAllTasks() {
        viewModelScope.launch {
            try {
                repository.clearTasks()
                _userMessage.value = "Todas as tarefas foram removidas."
            } catch (e: Exception) {
                Timber.e(e, "TaskViewModel: Failed to clear all tasks")
            }
        }
    }

    /**
     * Updates the active filter tab (All, Pending, Completed, Today, High Priority).
     */
    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    /**
     * Updates the active category filter chip.
     */
    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    /**
     * Updates the search query.
     */
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Updates the sort order.
     */
    fun setSortOption(sortOption: TaskSortOption) {
        _sortOption.value = sortOption
    }

    /**
     * Clears transient user feedback messages.
     */
    fun clearUserMessage() {
        _userMessage.value = null
    }

    private fun buildUiState(
        allTasks: List<TaskEntity>,
        filter: TaskFilter,
        category: String,
        query: String,
        sortOption: TaskSortOption,
        isLoading: Boolean,
        message: String?
    ): TaskUiState {
        val totalCount = allTasks.size
        val pendingCount = allTasks.count { !it.isCompleted }
        val completedCount = allTasks.count { it.isCompleted }
        val percentage = if (totalCount > 0) ((completedCount.toFloat() / totalCount.toFloat()) * 100).toInt() else 0

        // 1. Filter by status / time
        val filteredByStatus = when (filter) {
            TaskFilter.ALL -> allTasks
            TaskFilter.PENDING -> allTasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> allTasks.filter { it.isCompleted }
            TaskFilter.TODAY -> allTasks.filter { it.dueDate.equals("Hoje", ignoreCase = true) }
            TaskFilter.HIGH_PRIORITY -> allTasks.filter { it.priority.equals("HIGH", ignoreCase = true) || it.priority.equals("URGENT", ignoreCase = true) }
        }

        // 2. Filter by category
        val filteredByCategory = if (category == "Todos") {
            filteredByStatus
        } else {
            filteredByStatus.filter { it.category.equals(category, ignoreCase = true) }
        }

        // 3. Filter by search query (filter tasks by title or category in real-time)
        val filteredBySearch = if (query.isBlank()) {
            filteredByCategory
        } else {
            val q = query.trim().lowercase()
            val candidateList = if (category != "Todos" && filteredByCategory.none {
                it.title.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.taskCategory.displayName.lowercase().contains(q) ||
                it.taskCategory.key.lowercase().contains(q)
            }) {
                filteredByStatus
            } else {
                filteredByCategory
            }

            candidateList.filter { task ->
                task.title.lowercase().contains(q) ||
                task.category.lowercase().contains(q) ||
                task.taskCategory.displayName.lowercase().contains(q) ||
                task.taskCategory.key.lowercase().contains(q) ||
                task.description.lowercase().contains(q) ||
                task.tags.lowercase().contains(q)
            }
        }

        // 4. Sort
        val sortedTasks = when (sortOption) {
            TaskSortOption.PRIORITY_DESC -> filteredBySearch.sortedWith(
                compareBy(
                    { it.isCompleted }, // uncompleted first
                    {
                        when (it.priority.uppercase()) {
                            "URGENT" -> 0
                            "HIGH" -> 1
                            "MEDIUM" -> 2
                            "LOW" -> 3
                            else -> 4
                        }
                    },
                    { -it.createdAt }
                )
            )
            TaskSortOption.DUE_DATE_ASC -> filteredBySearch.sortedWith(
                compareBy({ it.isCompleted }, { it.dueDate })
            )
            TaskSortOption.CREATED_AT_DESC -> filteredBySearch.sortedByDescending { it.createdAt }
            TaskSortOption.TITLE_ASC -> filteredBySearch.sortedBy { it.title.lowercase() }
        }

        val dynamicCategories = (listOf("Todos", "Pessoal", "Trabalho", "Saúde", "Estudos", "Finanças", "Geral") + (allTasks.map { it.category }.filter { it.isNotBlank() })).distinct()

        return TaskUiState(
            tasks = sortedTasks,
            allTasks = allTasks,
            totalCount = totalCount,
            pendingCount = pendingCount,
            completedCount = completedCount,
            completionPercentage = percentage,
            selectedFilter = filter,
            selectedCategory = category,
            availableCategories = dynamicCategories.distinct(),
            searchQuery = query,
            sortOption = sortOption,
            isLoading = isLoading,
            userMessage = message
        )
    }

    /**
     * Factory for creating [TaskViewModel] instances.
     */
    class Factory(
        private val repository: TaskRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
                return TaskViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

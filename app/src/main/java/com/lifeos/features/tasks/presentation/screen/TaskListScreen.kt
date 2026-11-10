package com.lifeos.features.tasks.presentation.screen

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.database.AppDatabase
import com.example.core.database.TaskEntity
import com.example.core.database.TaskRepository
import com.example.core.ui.ResponsiveScreenContainer
import com.lifeos.features.tasks.presentation.components.AddTaskBottomSheet
import com.lifeos.features.tasks.presentation.components.AddTaskDialog
import com.lifeos.features.tasks.presentation.components.AddTaskFloatingActionButton
import com.lifeos.features.tasks.presentation.viewmodel.TaskFilter
import com.lifeos.features.tasks.presentation.viewmodel.TaskSortOption
import com.lifeos.features.tasks.presentation.viewmodel.TaskUiState
import com.lifeos.features.tasks.presentation.viewmodel.TaskViewModel

/**
 * Jetpack Compose screen that displays a list of tasks, implementing a LazyColumn
 * to observe and render the state from [TaskViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddBottomSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    // Show transient user feedback
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearUserMessage()
        }
    }

    ResponsiveScreenContainer {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tarefas",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    },
                    navigationIcon = {
                        if (onNavigateBack != null) {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar"
                                )
                            }
                        }
                    },
                    actions = {
                        // Sort menu button
                        Box {
                            IconButton(
                                onClick = { showSortMenu = true },
                                modifier = Modifier.testTag("sort_menu_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Sort,
                                    contentDescription = "Ordenar Tarefas"
                                )
                            }
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                TaskSortOption.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.displayName) },
                                        onClick = {
                                            viewModel.setSortOption(option)
                                            showSortMenu = false
                                        },
                                        leadingIcon = {
                                            if (uiState.sortOption == option) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        // Delete completed tasks
                        if (uiState.completedCount > 0) {
                            IconButton(
                                onClick = { viewModel.deleteCompletedTasks() },
                                modifier = Modifier.testTag("delete_completed_tasks_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Limpar Concluídas",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            floatingActionButton = {
                AddTaskFloatingActionButton(
                    onClick = { showAddBottomSheet = true }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Real-time Search bar at the top of the task list to filter by title or category
                TaskSearchBar(
                    query = uiState.searchQuery,
                    onQueryChanged = { viewModel.setSearchQuery(it) },
                    resultsCount = if (uiState.searchQuery.isNotBlank()) uiState.tasks.size else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    // 1. Metrics & Overview Header Card
                    item(key = "metrics_card") {
                        TaskOverviewHeaderCard(uiState = uiState)
                    }

                    // 2. Filter Chips Section
                    item(key = "filters_section") {
                        TaskFilterChips(
                            uiState = uiState,
                            onFilterSelected = { viewModel.setFilter(it) },
                            onCategorySelected = { viewModel.setCategory(it) }
                        )
                    }

                    // 3. Task List Section / Empty State
                    if (uiState.tasks.isEmpty()) {
                        item(key = "empty_state") {
                            TaskEmptyView(
                                filter = uiState.selectedFilter,
                                searchQuery = uiState.searchQuery,
                                onAddTask = { showAddBottomSheet = true }
                            )
                        }
                    } else {
                        items(
                            items = uiState.tasks,
                            key = { it.id }
                        ) { task ->
                            TaskListItemCard(
                                task = task,
                                onToggleCompletion = { viewModel.toggleTaskCompletion(task.id) },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Task Bottom Sheet component
    if (showAddBottomSheet) {
        AddTaskBottomSheet(
            viewModel = viewModel,
            onDismiss = { showAddBottomSheet = false }
        )
    }
}

/**
 * Convenient factory overload of [TaskListScreen] creating [TaskViewModel] from Room database.
 */
@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { TaskRepository(database.taskDao()) }
    val viewModel: TaskViewModel = viewModel(
        factory = TaskViewModel.Factory(repository)
    )
    TaskListScreen(
        viewModel = viewModel,
        modifier = modifier,
        onNavigateBack = onNavigateBack
    )
}

/**
 * Overview card showing progress, counters, and Room local persistence status.
 */
@Composable
private fun TaskOverviewHeaderCard(
    uiState: TaskUiState,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = if (uiState.totalCount > 0) {
            uiState.completedCount.toFloat() / uiState.totalCount.toFloat()
        } else 0f,
        label = "task_progress_anim"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progresso de Conclusão",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Room SQLite Database",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "${uiState.completedCount}/${uiState.totalCount} (${uiState.completionPercentage}%)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("task_progress_percentage_text")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricChip(label = "Total", count = uiState.totalCount, color = MaterialTheme.colorScheme.onSurface)
                MetricChip(label = "Pendentes", count = uiState.pendingCount, color = Color(0xFFF57C00))
                MetricChip(label = "Concluídas", count = uiState.completedCount, color = Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    count: Int,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Real-time search bar placed at the top of the task list to filter tasks by title or category.
 */
@Composable
fun TaskSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Pesquisar por título ou categoria (ex: Trabalho, Saúde)...",
    resultsCount: Int? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Pesquisar",
                    tint = if (query.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChanged("") },
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpar busca",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("task_search_bar")
                .testTag("task_search_field")
        )

        // Real-time active filter indicator banner
        if (query.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (resultsCount != null) {
                            "Filtrando: \"$query\" ($resultsCount encontrada${if (resultsCount == 1) "" else "s"})"
                        } else {
                            "Filtrando por título ou categoria: \"$query\""
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Limpar filtro",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onQueryChanged("") }
                        .padding(2.dp)
                        .testTag("clear_filter_action")
                )
            }
        }
    }
}

/**
 * Filter chips for task list: status tabs and category chips.
 */
@Composable
fun TaskFilterChips(
    uiState: TaskUiState,
    onFilterSelected: (TaskFilter) -> Unit,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Status Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(TaskFilter.entries) { filter ->
                val isSelected = uiState.selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSelected(filter) },
                    label = { Text(filter.displayName) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("filter_chip_${filter.name}")
                )
            }
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(uiState.availableCategories) { category ->
                val isSelected = uiState.selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category, style = MaterialTheme.typography.labelSmall) },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }
    }
}

/**
 * Filter controls combining real-time search bar and filter chips.
 */
@Composable
fun TaskFilterControls(
    uiState: TaskUiState,
    onFilterSelected: (TaskFilter) -> Unit,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TaskSearchBar(
            query = uiState.searchQuery,
            onQueryChanged = onSearchQueryChanged,
            resultsCount = if (uiState.searchQuery.isNotBlank()) uiState.tasks.size else null
        )
        TaskFilterChips(
            uiState = uiState,
            onFilterSelected = onFilterSelected,
            onCategorySelected = onCategorySelected
        )
    }
}

/**
 * Task item card rendered in LazyColumn.
 */
@Composable
private fun TaskListItemCard(
    task: TaskEntity,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCompleted = task.isCompleted
    val scaleState by animateFloatAsState(
        targetValue = if (isCompleted) 0.99f else 1f,
        animationSpec = spring(dampingRatio = 0.7f),
        label = "task_scale_anim"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = BorderStroke(
            1.dp,
            if (isCompleted) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .scale(scaleState)
            .testTag("task_item_${task.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Material 3 Checkbox to toggle completion status
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleCompletion() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline,
                        checkmarkColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .testTag("task_checkbox_${task.id}")
                        .testTag("toggle_task_${task.id}")
                )

                Spacer(modifier = Modifier.width(14.dp))

                // Title & Description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Delete Icon
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("delete_task_${task.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Excluir tarefa",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metadata Chips: Priority, Due Date, Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Priority Tag
                PriorityBadge(priority = task.priority)

                // Due Date Badge
                if (task.dueDate.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = task.dueDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Category Tag
                CategoryBadge(
                    category = task.category,
                    modifier = Modifier.testTag("task_category_badge_${task.id}")
                )
            }
        }
    }
}

@Composable
fun CategoryBadge(
    category: String,
    modifier: Modifier = Modifier
) {
    val taskCategory = com.example.core.database.TaskCategory.fromString(category)
    val color = Color(taskCategory.colorHex)
    val icon = when (taskCategory) {
        com.example.core.database.TaskCategory.PERSONAL -> Icons.Default.Person
        com.example.core.database.TaskCategory.WORK -> Icons.Default.Work
        com.example.core.database.TaskCategory.HEALTH -> Icons.Default.Favorite
        com.example.core.database.TaskCategory.STUDIES -> Icons.Default.School
        com.example.core.database.TaskCategory.FINANCE -> Icons.Default.AccountBalanceWallet
        com.example.core.database.TaskCategory.GENERAL -> Icons.Default.Category
    }

    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = taskCategory.displayName,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = color
            )
        }
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val (bgColor, textColor, label) = when (priority.uppercase()) {
        "URGENT" -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Urgente")
        "HIGH" -> Triple(Color(0xFFFFE0B2), Color(0xFFE65100), "Alta")
        "MEDIUM" -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "Média")
        else -> Triple(Color(0xFFF5F5F5), Color(0xFF616161), "Baixa")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
        }
    }
}

/**
 * Empty state presentation when list has no tasks.
 */
@Composable
private fun TaskEmptyView(
    filter: TaskFilter,
    searchQuery: String,
    onAddTask: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (searchQuery.isNotBlank()) Icons.Default.SearchOff else Icons.Default.Checklist,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (searchQuery.isNotBlank()) {
                    "Nenhuma tarefa encontrada para '$searchQuery'"
                } else when (filter) {
                    TaskFilter.COMPLETED -> "Nenhuma tarefa concluída ainda."
                    TaskFilter.PENDING -> "Tudo em dia! Nenhuma tarefa pendente."
                    TaskFilter.TODAY -> "Nenhuma tarefa agendada para hoje."
                    TaskFilter.HIGH_PRIORITY -> "Nenhuma tarefa de alta prioridade."
                    else -> "Nenhuma tarefa cadastrada no momento."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAddTask,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("empty_view_add_task_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Criar Nova Tarefa")
            }
        }
    }
}

/**
 * Dialog for defining and adding a new task.
 */
/**
 * Dialog for defining and adding a new task.
 */
@Composable
fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, description: String, priority: String, dueDate: String, category: String, tags: String) -> Unit
) {
    AddTaskDialog(
        onDismiss = onDismiss,
        onSaveTask = { title, description, priority, dueDate, category ->
            onConfirm(title, description, priority, dueDate, category, "")
        }
    )
}


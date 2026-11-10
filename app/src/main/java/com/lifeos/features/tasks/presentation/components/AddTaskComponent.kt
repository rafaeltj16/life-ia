package com.lifeos.features.tasks.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.database.TaskCategory
import com.lifeos.features.tasks.presentation.viewmodel.TaskViewModel

/**
 * Floating Action Button configured specifically for adding a new task.
 */
@Composable
fun AddTaskFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "Nova Tarefa",
    isExtended: Boolean = true
) {
    if (isExtended) {
        ExtendedFloatingActionButton(
            onClick = onClick,
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            text = {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .testTag("add_task_fab")
                .testTag("floating_action_button_add_task")
        )
    } else {
        FloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
                .testTag("add_task_fab")
                .testTag("floating_action_button_add_task")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * Material 3 Dropdown Menu Selector for Task Categories (e.g., Personal, Work, Health, etc.).
 * Includes localized display names, matching category color indicators, and icons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdownSelector(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Categoria",
    testTagSuffix: String = ""
) {
    var expanded by remember { mutableStateOf(false) }
    val currentCategory = TaskCategory.fromString(selectedCategory)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = currentCategory.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            leadingIcon = {
                val icon = when (currentCategory) {
                    TaskCategory.PERSONAL -> Icons.Default.Person
                    TaskCategory.WORK -> Icons.Default.Work
                    TaskCategory.HEALTH -> Icons.Default.Favorite
                    TaskCategory.STUDIES -> Icons.Default.School
                    TaskCategory.FINANCE -> Icons.Default.AccountBalanceWallet
                    TaskCategory.GENERAL -> Icons.Default.Category
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(currentCategory.colorHex),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .testTag("category_dropdown_selector$testTagSuffix")
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.testTag("category_dropdown_menu$testTagSuffix")
        ) {
            TaskCategory.entries.forEach { categoryOption ->
                val isSelected = currentCategory == categoryOption
                val categoryColor = Color(categoryOption.colorHex)
                val icon = when (categoryOption) {
                    TaskCategory.PERSONAL -> Icons.Default.Person
                    TaskCategory.WORK -> Icons.Default.Work
                    TaskCategory.HEALTH -> Icons.Default.Favorite
                    TaskCategory.STUDIES -> Icons.Default.School
                    TaskCategory.FINANCE -> Icons.Default.AccountBalanceWallet
                    TaskCategory.GENERAL -> Icons.Default.Category
                }

                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = categoryOption.displayName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "(${categoryOption.key})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(categoryColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = categoryColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selecionado",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    onClick = {
                        onCategorySelected(categoryOption.displayName)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    modifier = Modifier.testTag("category_option_${categoryOption.name}")
                )
            }
        }
    }
}

/**
 * Material 3 Modal Bottom Sheet component that allows users to define a new task
 * with title, description, priority, due date, and category.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    onSaveTask: (title: String, description: String, priority: String, dueDate: String, category: String) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier.testTag("add_task_bottom_sheet")
    ) {
        AddTaskSheetContent(
            onDismiss = onDismiss,
            onSaveTask = onSaveTask
        )
    }
}

/**
 * Reusable content layout for adding a task with title, description, and category dropdown,
 * used in bottom sheets, dialogs, or embedded views.
 */
@Composable
fun AddTaskSheetContent(
    onDismiss: () -> Unit,
    onSaveTask: (title: String, description: String, priority: String, dueDate: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var dueDate by remember { mutableStateOf("Hoje") }
    var category by remember { mutableStateOf(TaskCategory.PERSONAL.displayName) }
    var titleHasError by remember { mutableStateOf(false) }

    val priorities = listOf(
        "LOW" to ("Baixa" to Color(0xFF4CAF50)),
        "MEDIUM" to ("Média" to Color(0xFFFFA000)),
        "HIGH" to ("Alta" to Color(0xFFF57C00)),
        "URGENT" to ("Urgente" to Color(0xFFE53935))
    )
    val dueDates = listOf("Hoje", "Amanhã", "Esta semana", "Sem data")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Nova Tarefa",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Defina os detalhes e salve em seu banco local",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_add_task_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Fechar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Title field
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                if (it.isNotBlank()) titleHasError = false
            },
            label = { Text("Título da Tarefa *") },
            placeholder = { Text("Ex: Finalizar proposta de projeto") },
            singleLine = true,
            isError = titleHasError,
            supportingText = {
                if (titleHasError) {
                    Text("O título da tarefa é obrigatório.", color = MaterialTheme.colorScheme.error)
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = if (titleHasError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_task_title")
                .testTag("add_task_title_input")
        )

        // Description field
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição e Notas (opcional)") },
            placeholder = { Text("Adicione detalhes, links ou contexto...") },
            minLines = 3,
            maxLines = 5,
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline
                )
            },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_task_description")
                .testTag("add_task_description_input")
        )

        // Category dropdown selector
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Categoria",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            CategoryDropdownSelector(
                selectedCategory = category,
                onCategorySelected = { category = it },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Priority selection
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Nível de Prioridade",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(priorities) { (key, data) ->
                    val (label, color) = data
                    val isSelected = priority == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { priority = key },
                        label = { Text(label) },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.onPrimary else color)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_task_priority_chip_$key")
                    )
                }
            }
        }

        // Due Date selection
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Data de Vencimento",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dueDates) { option ->
                    val isSelected = dueDate == option
                    FilterChip(
                        selected = isSelected,
                        onClick = { dueDate = option },
                        label = { Text(option) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_task_due_date_chip_$option")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("cancel_button")
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleHasError = true
                    } else {
                        onSaveTask(
                            title.trim(),
                            description.trim(),
                            priority,
                            dueDate,
                            category
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1.5f)
                    .height(48.dp)
                    .testTag("confirm_add_task_button")
                    .testTag("save_task_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salvar Tarefa",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

/**
 * Overload of [AddTaskBottomSheet] that directly coordinates with [TaskViewModel].
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AddTaskBottomSheet(
        onDismiss = onDismiss,
        onSaveTask = { title, description, priority, dueDate, category ->
            viewModel.addTask(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                category = category
            )
            onDismiss()
        },
        modifier = modifier
    )
}

/**
 * Material 3 Dialog alternative to add a new task with title, description, and category dropdown.
 */
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onSaveTask: (title: String, description: String, priority: String, dueDate: String, category: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("MEDIUM") }
    var dueDate by remember { mutableStateOf("Hoje") }
    var category by remember { mutableStateOf(TaskCategory.PERSONAL.displayName) }
    var titleHasError by remember { mutableStateOf(false) }

    val priorities = listOf("LOW" to "Baixa", "MEDIUM" to "Média", "HIGH" to "Alta", "URGENT" to "Urgente")
    val dueDates = listOf("Hoje", "Amanhã", "Esta semana", "Sem data")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nova Tarefa", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleHasError = false
                    },
                    label = { Text("Título da Tarefa *") },
                    placeholder = { Text("Ex: Finalizar proposta comercial") },
                    singleLine = true,
                    isError = titleHasError,
                    supportingText = {
                        if (titleHasError) {
                            Text("O título é obrigatório", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_title")
                        .testTag("add_task_title_input")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (opcional)") },
                    placeholder = { Text("Adicione detalhes ou notas de execução") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_task_description")
                        .testTag("add_task_description_input")
                )

                // Category dropdown selector in dialog
                Text("Categoria:", style = MaterialTheme.typography.labelMedium)
                CategoryDropdownSelector(
                    selectedCategory = category,
                    onCategorySelected = { category = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Prioridade:", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(priorities) { (key, label) ->
                        FilterChip(
                            selected = priority == key,
                            onClick = { priority = key },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("dialog_priority_chip_$key")
                        )
                    }
                }

                Text("Vencimento:", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(dueDates) { option ->
                        FilterChip(
                            selected = dueDate == option,
                            onClick = { dueDate = option },
                            label = { Text(option, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("dialog_due_date_chip_$option")
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleHasError = true
                    } else {
                        onSaveTask(
                            title.trim(),
                            description.trim(),
                            priority,
                            dueDate,
                            category
                        )
                    }
                },
                modifier = Modifier
                    .testTag("confirm_add_task_button")
                    .testTag("save_task_button")
            ) {
                Text("Adicionar Tarefa")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("cancel_add_task_button")
            ) {
                Text("Cancelar")
            }
        },
        modifier = modifier
    )
}

/**
 * Overload of [AddTaskDialog] that connects directly to [TaskViewModel].
 */
@Composable
fun AddTaskDialog(
    viewModel: TaskViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AddTaskDialog(
        onDismiss = onDismiss,
        onSaveTask = { title, description, priority, dueDate, category ->
            viewModel.addTask(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate,
                category = category
            )
            onDismiss()
        },
        modifier = modifier
    )
}

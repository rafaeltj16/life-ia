package com.lifeos.features.tasks.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ui.*
import com.example.core.common.*
import com.example.core.common.LifeOSViewModel
import com.lifeos.features.tasks.presentation.viewmodel.TaskViewModel

// --- TASKS SCREEN ---
@Composable
fun TasksScreen(taskViewModel: TaskViewModel) {
    TaskListScreen(viewModel = taskViewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(viewModel: LifeOSViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val projects by viewModel.projects.collectAsState()
    val aiSuggestion by viewModel.aiTaskSuggestion.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var newTaskStatus by remember { mutableStateOf(TaskStatus.TO_DO) }
    var selectedProjectId by remember { mutableStateOf<String?>(null) }

    var isLocalMode by remember { mutableStateOf(true) }
    var cloudStatusFilter by remember { mutableStateOf(TaskStatus.TO_DO) }
    var cloudFilterCategory by remember { mutableStateOf("Hoje") }
    var newTaskDueDate by remember { mutableStateOf("Hoje") }
    var newTaskIsFavorite by remember { mutableStateOf(false) }
    var activeTagFilter by remember { mutableStateOf<String?>(null) }
    var selectedTags by remember { mutableStateOf(emptySet<String>()) }
    var cloudViewMode by remember { mutableStateOf("lista") }

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Minhas Tarefas", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = if (isLocalMode) "Gerenciamento em estado local" else "Sincronizado com LifeOS Cloud",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                if (!isLocalMode) {
                    LifeOSButton(
                        text = "Nova Tarefa",
                        onClick = { showAddDialog = true },
                        icon = Icons.Default.Add,
                        modifier = Modifier.testTag("add_task_button")
                    )
                }
            }

            // Mode Selector Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isLocalMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { isLocalMode = true }
                        .padding(vertical = 10.dp)
                        .testTag("task_mode_local"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Rascunho Local",
                        color = if (isLocalMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (!isLocalMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { isLocalMode = false }
                        .padding(vertical = 10.dp)
                        .testTag("task_mode_cloud"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "LifeOS Cloud",
                        color = if (!isLocalMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            if (isLocalMode) {
                LocalTaskComponent()
            } else {
                // Embedded AI suggestions trigger button inside main screen
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.generateAITasksSuggestions() }
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                        .border(0.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Perguntar ao Gemini sugestões de tarefas focadas",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }

                if (aiSuggestion != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = aiSuggestion ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }

                // View Mode Selection Row (Lista vs Fluxo) for Cloud mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (cloudViewMode == "lista") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .border(1.dp, if (cloudViewMode == "lista") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .clickable { cloudViewMode = "lista" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (cloudViewMode == "lista") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Lista de Tarefas",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (cloudViewMode == "lista") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (cloudViewMode == "fluxo") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .border(1.dp, if (cloudViewMode == "fluxo") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .clickable { cloudViewMode = "fluxo" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = Icons.Default.AssignmentTurnedIn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (cloudViewMode == "fluxo") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Fluxo de Status",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (cloudViewMode == "fluxo") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (cloudViewMode == "lista") {
                    // 8 Category Filter Dashboard Grid (2 columns)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val row1 = listOf(
                            "Hoje" to Triple(Icons.Default.Today, MaterialTheme.colorScheme.primary, tasks.count { it.dueDate.equals("Hoje", ignoreCase = true) && !it.isCompleted }),
                            "Amanhã" to Triple(Icons.Default.Schedule, Color(0xFFFFA726), tasks.count { it.dueDate.equals("Amanhã", ignoreCase = true) && !it.isCompleted })
                        )
                        val row2 = listOf(
                            "Semana" to Triple(Icons.Default.DateRange, Color(0xFF29B6F6), tasks.count { it.dueDate.equals("Semana", ignoreCase = true) && !it.isCompleted }),
                            "Mês" to Triple(Icons.Default.DateRange, Color(0xFF7E57C2), tasks.count { it.dueDate.equals("Mês", ignoreCase = true) && !it.isCompleted })
                        )
                        val row3 = listOf(
                            "Concluídas" to Triple(Icons.Default.Check, Color(0xFF81C784), tasks.count { it.isCompleted }),
                            "Pendentes" to Triple(Icons.Default.AssignmentTurnedIn, Color(0xFF90A4AE), tasks.count { !it.isCompleted })
                        )
                        val row4 = listOf(
                            "Atrasadas" to Triple(Icons.Default.AssignmentTurnedIn, Color(0xFFEF5350), tasks.count { (it.dueDate.equals("Atrasadas", ignoreCase = true) || it.dueDate.equals("Atrasada", ignoreCase = true)) && !it.isCompleted }),
                            "Favoritas" to Triple(Icons.Default.Star, Color(0xFFFFB74D), tasks.count { it.isFavorite })
                        )
                        
                        listOf(row1, row2, row3, row4).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { (category, data) ->
                                    val (icon, color, count) = data
                                    val isSelected = cloudFilterCategory == category
                                    
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isSelected) color.copy(alpha = 0.12f) 
                                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                            )
                                            .border(
                                                width = if (isSelected) 2.dp else 1.dp,
                                                color = if (isSelected) color else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable { cloudFilterCategory = category }
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                            .testTag("filter_cloud_$category")
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape)
                                                        .background(color.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = null,
                                                        tint = color,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                Text(
                                                    text = category,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                            
                                            // Count Badge
                                            Box(
                                                modifier = Modifier
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) color.copy(alpha = 0.25f)
                                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = count.toString(),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Tag Filter Chips for Cloud mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val isNoTagFilter = activeTagFilter == null
                        FilterChip(
                            selected = isNoTagFilter,
                            onClick = { activeTagFilter = null },
                            label = { Text("Todas as Tags", style = MaterialTheme.typography.bodySmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                        listOf("Urgente", "Android", "Supabase", "IA", "Casa", "Mercado").forEach { tag ->
                            val isSelected = activeTagFilter == tag
                            val tagColor = when (tag) {
                                "Urgente" -> Color(0xFFEF5350)
                                "Android" -> Color(0xFF81C784)
                                "Supabase" -> Color(0xFF24B47E)
                                "IA" -> Color(0xFF7E57C2)
                                "Casa" -> Color(0xFF29B6F6)
                                "Mercado" -> Color(0xFFFFA726)
                                else -> MaterialTheme.colorScheme.primary
                            }
                            FilterChip(
                                selected = isSelected,
                                onClick = { activeTagFilter = if (isSelected) null else tag },
                                label = { Text("#$tag", style = MaterialTheme.typography.bodySmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = tagColor.copy(alpha = 0.15f),
                                    selectedLabelColor = tagColor
                                )
                            )
                        }
                    }

                    val filteredTasks = tasks.filter { task ->
                        val matchesCategory = when (cloudFilterCategory) {
                            "Hoje" -> task.dueDate.equals("Hoje", ignoreCase = true)
                            "Amanhã" -> task.dueDate.equals("Amanhã", ignoreCase = true)
                            "Semana" -> task.dueDate.equals("Semana", ignoreCase = true)
                            "Mês" -> task.dueDate.equals("Mês", ignoreCase = true)
                            "Concluídas" -> task.isCompleted || task.status == TaskStatus.COMPLETED
                            "Pendentes" -> !task.isCompleted && task.status != TaskStatus.COMPLETED
                            "Atrasadas" -> task.dueDate.equals("Atrasadas", ignoreCase = true) || task.dueDate.equals("Atrasada", ignoreCase = true)
                            "Favoritas" -> task.isFavorite
                            else -> true
                        }
                        val matchesTag = activeTagFilter == null || task.tags.contains(activeTagFilter)
                        matchesCategory && matchesTag
                    }

                    if (filteredTasks.isEmpty()) {
                        EmptyStateView(
                            title = "Nenhuma tarefa em '$cloudFilterCategory'",
                            description = "Crie uma nova tarefa com este prazo ou altere as existentes para visualizar aqui."
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(filteredTasks, key = { it.id }) { task ->
                                var showStatusDropdown by remember { mutableStateOf(false) }
                                var showSubtasks by remember { mutableStateOf(false) }
                                var newSubtaskTitle by remember { mutableStateOf("") }

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = task.isCompleted,
                                            onCheckedChange = { viewModel.toggleTask(task.id) },
                                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                                            modifier = Modifier.testTag("task_checkbox_${task.id}")
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                task.title,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(task.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("• ${task.dueDate}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                                task.projectId?.let { pId ->
                                                    projects.find { it.id == pId }?.let { proj ->
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("•", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .size(8.dp)
                                                                .clip(CircleShape)
                                                                .background(Color(android.graphics.Color.parseColor(proj.color)))
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(proj.name, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(android.graphics.Color.parseColor(proj.color)))
                                                    }
                                                }
                                            }
                                            if (task.tags.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Row(
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                    modifier = Modifier.horizontalScroll(rememberScrollState())
                                                ) {
                                                    task.tags.forEach { tag ->
                                                        val tagColor = when (tag) {
                                                            "Urgente" -> Color(0xFFEF5350)
                                                            "Android" -> Color(0xFF81C784)
                                                            "Supabase" -> Color(0xFF24B47E)
                                                            "IA" -> Color(0xFF7E57C2)
                                                            "Casa" -> Color(0xFF29B6F6)
                                                            "Mercado" -> Color(0xFFFFA726)
                                                            else -> MaterialTheme.colorScheme.primary
                                                        }
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(tagColor.copy(alpha = 0.12f))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = "#$tag",
                                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                                                color = tagColor
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        val priorityColor = when (task.priority) {
                                            TaskPriority.HIGH -> Color(0xFFEF5350)
                                            TaskPriority.MEDIUM -> Color(0xFFFFB74D)
                                            TaskPriority.LOW -> Color(0xFF81C784)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            // Favorite Star Button for Cloud Mode
                                            IconButton(
                                                onClick = { viewModel.toggleTaskFavorite(task.id) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (task.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                                    contentDescription = "Favorito",
                                                    tint = if (task.isFavorite) Color(0xFFFFB74D) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            LifeOSBadge(text = task.priority.name, color = priorityColor)
                                            
                                            // Status Selector Dropdown
                                            Box {
                                                val statusBadgeColor = when (task.status) {
                                                    TaskStatus.TO_DO -> Color(0xFF90A4AE)
                                                    TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                                                    TaskStatus.WAITING -> Color(0xFFFFB74D)
                                                    TaskStatus.COMPLETED -> Color(0xFF81C784)
                                                    TaskStatus.ARCHIVED -> MaterialTheme.colorScheme.outline
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(statusBadgeColor.copy(alpha = 0.15f))
                                                        .border(1.dp, statusBadgeColor, RoundedCornerShape(8.dp))
                                                        .clickable { showStatusDropdown = true }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                                        Text(
                                                            text = task.status.displayName,
                                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                            color = statusBadgeColor
                                                        )
                                                        Icon(
                                                            imageVector = Icons.Default.ArrowDropDown,
                                                            contentDescription = null,
                                                            tint = statusBadgeColor,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }

                                                DropdownMenu(
                                                    expanded = showStatusDropdown,
                                                    onDismissRequest = { showStatusDropdown = false }
                                                ) {
                                                    TaskStatus.values().forEach { st ->
                                                        DropdownMenuItem(
                                                            text = { Text(st.displayName, style = MaterialTheme.typography.bodyMedium) },
                                                            onClick = {
                                                                viewModel.updateTaskStatus(task.id, st)
                                                                showStatusDropdown = false
                                                            }
                                                        )
                                                    }
                                                }
                                            }

                                            IconButton(
                                                onClick = { viewModel.deleteTask(task.id) },
                                                modifier = Modifier.testTag("delete_task_${task.id}").size(32.dp)
                                            ) {
                                                Icon(Icons.Default.DeleteOutline, contentDescription = "Excluir tarefa", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    }

                                    // Checklist Indicators and Expand Button
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val completedCount = task.subTasks.count { it.completed }
                                        val totalCount = task.subTasks.size
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.List,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                text = if (totalCount > 0) "$completedCount/$totalCount sub-tarefas" else "Nenhuma sub-tarefa",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                            )
                                        }

                                        TextButton(
                                            onClick = { showSubtasks = !showSubtasks },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(
                                                text = if (showSubtasks) "Ocultar" else "Ver Checklist",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Icon(
                                                imageVector = if (showSubtasks) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    if (showSubtasks) {
                                        Divider(
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                        )

                                        // List existing subtasks
                                        task.subTasks.forEach { sub ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = sub.completed,
                                                    onCheckedChange = { viewModel.toggleSubTask(task.id, sub.id) },
                                                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary),
                                                    modifier = Modifier.size(32.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = sub.title,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        textDecoration = if (sub.completed) TextDecoration.LineThrough else null
                                                    ),
                                                    color = if (sub.completed) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                IconButton(
                                                    onClick = { viewModel.deleteSubTask(task.id, sub.id) },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Remover",
                                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Input to add a new subtask
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            OutlinedTextField(
                                                value = newSubtaskTitle,
                                                onValueChange = { newSubtaskTitle = it },
                                                placeholder = { Text("Adicionar item...", style = MaterialTheme.typography.bodySmall) },
                                                singleLine = true,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp),
                                                textStyle = MaterialTheme.typography.bodySmall,
                                                shape = RoundedCornerShape(8.dp),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                                )
                                            )

                                            IconButton(
                                                onClick = {
                                                    if (newSubtaskTitle.isNotBlank()) {
                                                        viewModel.addSubTask(task.id, newSubtaskTitle.trim())
                                                        newSubtaskTitle = ""
                                                    }
                                                },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "Adicionar",
                                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // PIPELINE FLOW: A Fazer -> Em andamento -> Concluído
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. A Fazer Section
                        val todoTasks = tasks.filter { it.status == TaskStatus.TO_DO }
                        PipelineLane(
                            title = "A Fazer",
                            count = todoTasks.size,
                            tasks = todoTasks,
                            accentColor = Color(0xFF90A4AE),
                            actionButtonText = "Começar Trabalho",
                            onActionClick = { task ->
                                viewModel.updateTaskStatus(task.id, TaskStatus.IN_PROGRESS)
                            },
                            onDeleteClick = { task ->
                                viewModel.deleteTask(task.id)
                            }
                        )

                        // Down Arrow Icon "↓"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "↓",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // 2. Em andamento Section
                        val inProgressTasks = tasks.filter { it.status == TaskStatus.IN_PROGRESS }
                        PipelineLane(
                            title = "Em andamento",
                            count = inProgressTasks.size,
                            tasks = inProgressTasks,
                            accentColor = MaterialTheme.colorScheme.primary,
                            actionButtonText = "Marcar Concluído",
                            onActionClick = { task ->
                                viewModel.updateTaskStatus(task.id, TaskStatus.COMPLETED)
                            },
                            onDeleteClick = { task ->
                                viewModel.deleteTask(task.id)
                            }
                        )

                        // Down Arrow Icon "↓"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "↓",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // 3. Concluído Section
                        val completedTasks = tasks.filter { it.status == TaskStatus.COMPLETED }
                        PipelineLane(
                            title = "Concluído",
                            count = completedTasks.size,
                            tasks = completedTasks,
                            accentColor = Color(0xFF81C784),
                            actionButtonText = "Mover para Fazer",
                            onActionClick = { task ->
                                viewModel.updateTaskStatus(task.id, TaskStatus.TO_DO)
                            },
                            onDeleteClick = { task ->
                                viewModel.deleteTask(task.id)
                            }
                        )
                    }
                }
            }

            if (showAddDialog) {
                LifeOSDialog(
                    title = "Criar Tarefa",
                    onDismissRequest = { showAddDialog = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    viewModel.addTask(title, category, priority, newTaskStatus, selectedTags.toList(), newTaskDueDate, newTaskIsFavorite, selectedProjectId)
                                    title = ""
                                    category = ""
                                    priority = TaskPriority.MEDIUM
                                    newTaskStatus = TaskStatus.TO_DO
                                    selectedTags = emptySet()
                                    newTaskDueDate = "Hoje"
                                    newTaskIsFavorite = false
                                    selectedProjectId = null
                                    showAddDialog = false
                                }
                            },
                            modifier = Modifier.testTag("confirm_add_task_button")
                        ) {
                            Text("Adicionar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                    }
                ) {
                    LifeOSInput(value = title, onValueChange = { title = it }, placeholder = "O que precisa fazer?")
                    Spacer(modifier = Modifier.height(8.dp))
                    LifeOSInput(value = category, onValueChange = { category = it }, placeholder = "Categoria (ex: Trabalho, Compras)")
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Prioridade", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskPriority.values().forEach { prio ->
                            val isSel = priority == prio
                            val color = when (prio) {
                                TaskPriority.HIGH -> Color(0xFFEF5350)
                                TaskPriority.MEDIUM -> Color(0xFFFFB74D)
                                TaskPriority.LOW -> Color(0xFF81C784)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSel) color.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isSel) color else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { priority = prio }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(prio.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSel) color else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Status Inicial", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TaskStatus.values().forEach { st ->
                            val isSel = newTaskStatus == st
                            val stColor = when (st) {
                                TaskStatus.TO_DO -> Color(0xFF90A4AE)
                                TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
                                TaskStatus.WAITING -> Color(0xFFFFB74D)
                                TaskStatus.COMPLETED -> Color(0xFF81C784)
                                TaskStatus.ARCHIVED -> MaterialTheme.colorScheme.outline
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (isSel) stColor.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isSel) stColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { newTaskStatus = st }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(st.displayName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSel) stColor else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    // Due Date (Prazo) and Favorite Selection
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Prazo (Vencimento)", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        
                        // Favorite Selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { newTaskIsFavorite = !newTaskIsFavorite }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (newTaskIsFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Favoritar",
                                tint = if (newTaskIsFavorite) Color(0xFFFFB74D) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Favorito",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (newTaskIsFavorite) Color(0xFFFFB74D) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Hoje", "Amanhã", "Semana", "Mês", "Atrasadas").forEach { dateOption ->
                            val isSel = newTaskDueDate == dateOption
                            val stColor = when (dateOption) {
                                "Hoje" -> MaterialTheme.colorScheme.primary
                                "Amanhã" -> Color(0xFFFFA726)
                                "Semana" -> Color(0xFF29B6F6)
                                "Mês" -> Color(0xFF7E57C2)
                                "Atrasadas" -> Color(0xFFEF5350)
                                else -> MaterialTheme.colorScheme.outline
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (isSel) stColor.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isSel) stColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { newTaskDueDate = dateOption }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(dateOption, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSel) stColor else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Tags", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Urgente", "Android", "Supabase", "IA", "Casa", "Mercado").forEach { tag ->
                            val isSel = selectedTags.contains(tag)
                            val tagColor = when (tag) {
                                "Urgente" -> Color(0xFFEF5350)
                                "Android" -> Color(0xFF81C784)
                                "Supabase" -> Color(0xFF24B47E)
                                "IA" -> Color(0xFF7E57C2)
                                "Casa" -> Color(0xFF29B6F6)
                                "Mercado" -> Color(0xFFFFA726)
                                else -> MaterialTheme.colorScheme.primary
                            }
                            Box(
                                modifier = Modifier
                                    .background(if (isSel) tagColor.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isSel) tagColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedTags = if (isSel) {
                                            selectedTags - tag
                                        } else {
                                            selectedTags + tag
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("#$tag", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSel) tagColor else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    if (projects.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Vincular ao Projeto", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val isNoneSel = selectedProjectId == null
                            Box(
                                modifier = Modifier
                                    .background(if (isNoneSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                    .border(1.dp, if (isNoneSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                    .clickable { selectedProjectId = null }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Nenhum", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isNoneSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                            }

                            projects.forEach { proj ->
                                val isSel = selectedProjectId == proj.id
                                val projColor = Color(android.graphics.Color.parseColor(proj.color))
                                Box(
                                    modifier = Modifier
                                        .background(if (isSel) projColor.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))
                                        .border(1.dp, if (isSel) projColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                                        .clickable { selectedProjectId = proj.id }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(projColor)
                                        )
                                        Text(proj.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSel) projColor else MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

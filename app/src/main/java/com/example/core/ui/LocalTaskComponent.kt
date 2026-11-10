package com.example.core.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.TaskItem
import com.example.core.common.TaskPriority
import com.example.core.common.TaskStatus
import java.util.UUID

@Composable
fun LocalTaskComponent(
    modifier: Modifier = Modifier
) {
    // Local in-memory state of tasks with different initial statuses
    val localTasks = remember {
        mutableStateListOf(
            TaskItem(
                id = UUID.randomUUID().toString(),
                title = "Comprar combustível",
                category = "Pessoal",
                isCompleted = false,
                dueDate = "Hoje",
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.TO_DO,
                tags = listOf("Casa"),
                isFavorite = true
            ),
            TaskItem(
                id = UUID.randomUUID().toString(),
                title = "Estudar Kotlin",
                category = "Estudos",
                isCompleted = false,
                dueDate = "Amanhã",
                priority = TaskPriority.HIGH,
                status = TaskStatus.TO_DO,
                tags = listOf("Android", "IA"),
                isFavorite = true
            ),
            TaskItem(
                id = UUID.randomUUID().toString(),
                title = "Academia",
                category = "Saúde",
                isCompleted = true,
                dueDate = "Hoje",
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.COMPLETED,
                tags = listOf("Casa")
            ),
            TaskItem(
                id = UUID.randomUUID().toString(),
                title = "Definir metas semanais do LifeOS",
                category = "Planejamento",
                isCompleted = false,
                dueDate = "Semana",
                priority = TaskPriority.HIGH,
                status = TaskStatus.TO_DO,
                tags = listOf("Urgente", "Android")
            ),
            TaskItem(
                id = UUID.randomUUID().toString(),
                title = "Organizar espaço de trabalho",
                category = "Organização",
                isCompleted = false,
                dueDate = "Mês",
                priority = TaskPriority.MEDIUM,
                status = TaskStatus.WAITING,
                tags = listOf("Casa")
            ),
            TaskItem(
                id = UUID.randomUUID().toString(),
                title = "Enviar declaração anual atrasada",
                category = "Finanças",
                isCompleted = false,
                dueDate = "Atrasadas",
                priority = TaskPriority.HIGH,
                status = TaskStatus.TO_DO,
                tags = listOf("Urgente")
            )
        )
    }

    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskCategory by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var selectedStatus by remember { mutableStateOf(TaskStatus.TO_DO) }
    var localStatusFilter by remember { mutableStateOf(TaskStatus.TO_DO) }
    var localActiveTagFilter by remember { mutableStateOf<String?>(null) }
    var localSelectedTags by remember { mutableStateOf(emptySet<String>()) }
    var localViewMode by remember { mutableStateOf("lista") }
    var selectedFilterCategory by remember { mutableStateOf("Hoje") }
    var newTaskDueDate by remember { mutableStateOf("Hoje") }
    var newTaskIsFavorite by remember { mutableStateOf(false) }

    val totalCount = localTasks.size
    val completedCount = localTasks.count { it.isCompleted }
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Component Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Lista Local de Tarefas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Estado local temporário • Sandbox",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }

            LifeOSBadge(
                text = "$completedCount/$totalCount Feitas",
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Progress Bar
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(progress * 100).toInt()}% concluído",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (totalCount > 0 && completedCount == totalCount) {
                    Text(
                        text = "Excelente! Tudo feito ✨",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Add Task Fields
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LifeOSInput(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it },
                placeholder = "Nome da tarefa local...",
                modifier = Modifier.testTag("local_task_title_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    LifeOSInput(
                        value = newTaskCategory,
                        onValueChange = { newTaskCategory = it },
                        placeholder = "Categoria (ex: Pessoal)",
                        modifier = Modifier.testTag("local_task_category_input")
                    )
                }

                // Add Button
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            val categoryText = if (newTaskCategory.isBlank()) "Geral" else newTaskCategory
                            localTasks.add(
                                TaskItem(
                                    id = UUID.randomUUID().toString(),
                                    title = newTaskTitle.trim(),
                                    category = categoryText.trim(),
                                    isCompleted = (selectedStatus == TaskStatus.COMPLETED),
                                    dueDate = newTaskDueDate,
                                    priority = selectedPriority,
                                    status = selectedStatus,
                                    tags = localSelectedTags.toList(),
                                    isFavorite = newTaskIsFavorite
                                )
                            )
                            newTaskTitle = ""
                            newTaskCategory = ""
                            selectedPriority = TaskPriority.MEDIUM
                            selectedStatus = TaskStatus.TO_DO
                            localSelectedTags = emptySet()
                            newTaskDueDate = "Hoje"
                            newTaskIsFavorite = false
                        }
                    },
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("local_task_add_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Criar", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }

            // Priority Selection
            Text(
                text = "Prioridade",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskPriority.values().forEach { prio ->
                    val isSelected = selectedPriority == prio
                    val priorityColor = when (prio) {
                        TaskPriority.HIGH -> Color(0xFFEF5350)
                        TaskPriority.MEDIUM -> Color(0xFFFFB74D)
                        TaskPriority.LOW -> Color(0xFF81C784)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) priorityColor.copy(alpha = 0.15f) else Color.Transparent)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) priorityColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedPriority = prio }
                            .padding(8.dp)
                            .testTag("local_task_priority_${prio.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prio.name,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) priorityColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Status Selection for Local Task Creation
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status Inicial",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TaskStatus.values().forEach { st ->
                    val isSel = selectedStatus == st
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
                            .border(1.dp, if (isSel) stColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable { selectedStatus = st }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(st.displayName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSel) stColor else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Due Date (Prazo) and Favorite Selection
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Prazo (Vencimento)",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Toggle Favorite Button
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
                            .border(1.dp, if (isSel) stColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable { newTaskDueDate = dateOption }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(dateOption, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSel) stColor else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            // Tag Selection for Local Task Creation
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tags",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("Urgente", "Android", "Supabase", "IA", "Casa", "Mercado").forEach { tag ->
                    val isSel = localSelectedTags.contains(tag)
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
                            .border(1.dp, if (isSel) tagColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable {
                                localSelectedTags = if (isSel) {
                                    localSelectedTags - tag
                                } else {
                                    localSelectedTags + tag
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("#$tag", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = if (isSel) tagColor else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

        // View Mode Selection Row (Lista vs Fluxo) for Local Mode
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
                    .background(if (localViewMode == "lista") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .border(1.dp, if (localViewMode == "lista") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .clickable { localViewMode = "lista" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (localViewMode == "lista") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Lista de Tarefas",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = if (localViewMode == "lista") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (localViewMode == "fluxo") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .border(1.dp, if (localViewMode == "fluxo") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .clickable { localViewMode = "fluxo" }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.AssignmentTurnedIn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (localViewMode == "fluxo") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Fluxo de Status",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = if (localViewMode == "fluxo") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (localViewMode == "lista") {
            // 8 Category Filter Dashboard Grid (2 columns)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val row1 = listOf(
                    "Hoje" to Triple(Icons.Default.Today, MaterialTheme.colorScheme.primary, localTasks.count { it.dueDate.equals("Hoje", ignoreCase = true) && !it.isCompleted }),
                    "Amanhã" to Triple(Icons.Default.Schedule, Color(0xFFFFA726), localTasks.count { it.dueDate.equals("Amanhã", ignoreCase = true) && !it.isCompleted })
                )
                val row2 = listOf(
                    "Semana" to Triple(Icons.Default.DateRange, Color(0xFF29B6F6), localTasks.count { it.dueDate.equals("Semana", ignoreCase = true) && !it.isCompleted }),
                    "Mês" to Triple(Icons.Default.DateRange, Color(0xFF7E57C2), localTasks.count { it.dueDate.equals("Mês", ignoreCase = true) && !it.isCompleted })
                )
                val row3 = listOf(
                    "Concluídas" to Triple(Icons.Default.Check, Color(0xFF81C784), localTasks.count { it.isCompleted }),
                    "Pendentes" to Triple(Icons.Default.AssignmentTurnedIn, Color(0xFF90A4AE), localTasks.count { !it.isCompleted })
                )
                val row4 = listOf(
                    "Atrasadas" to Triple(Icons.Default.AssignmentTurnedIn, Color(0xFFEF5350), localTasks.count { (it.dueDate.equals("Atrasadas", ignoreCase = true) || it.dueDate.equals("Atrasada", ignoreCase = true)) && !it.isCompleted }),
                    "Favoritas" to Triple(Icons.Default.Star, Color(0xFFFFB74D), localTasks.count { it.isFavorite })
                )
                
                listOf(row1, row2, row3, row4).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { (category, data) ->
                            val (icon, color, count) = data
                            val isSelected = selectedFilterCategory == category
                            
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
                                    .clickable { selectedFilterCategory = category }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("filter_local_$category")
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

            // Tag Filter Chips for Local Mode
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isNoTagFilter = localActiveTagFilter == null
                FilterChip(
                    selected = isNoTagFilter,
                    onClick = { localActiveTagFilter = null },
                    label = { Text("Todas as Tags", style = MaterialTheme.typography.bodySmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
                listOf("Urgente", "Android", "Supabase", "IA", "Casa", "Mercado").forEach { tag ->
                    val isSelected = localActiveTagFilter == tag
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
                        onClick = { localActiveTagFilter = if (isSelected) null else tag },
                        label = { Text("#$tag", style = MaterialTheme.typography.bodySmall) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = tagColor.copy(alpha = 0.15f),
                            selectedLabelColor = tagColor
                        )
                    )
                }
            }

            val filteredLocalTasks = localTasks.filter { task ->
                val matchesCategory = when (selectedFilterCategory) {
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
                val matchesTag = localActiveTagFilter == null || task.tags.contains(localActiveTagFilter)
                matchesCategory && matchesTag
            }

            // Tasks List
            if (filteredLocalTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Nenhuma tarefa em '$selectedFilterCategory'",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Crie ou mova tarefas locais para listar aqui.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredLocalTasks.forEach { task ->
                        var showStatusDropdown by remember { mutableStateOf(false) }

                        val priorityColor = when (task.priority) {
                            TaskPriority.HIGH -> Color(0xFFEF5350)
                            TaskPriority.MEDIUM -> Color(0xFFFFB74D)
                            TaskPriority.LOW -> Color(0xFF81C784)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(10.dp)
                                .testTag("local_task_item_${task.id}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Custom Minimalist Checkbox
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (task.isCompleted) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (task.isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        val index = localTasks.indexOfFirst { it.id == task.id }
                                        if (index != -1) {
                                            localTasks[index] = task.withCompleted(!task.isCompleted)
                                        }
                                    }
                                    .testTag("local_task_toggle_${task.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (task.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Concluído",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                                    ),
                                    color = if (task.isCompleted) {
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = task.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                    Text(
                                        text = task.priority.name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = priorityColor.copy(alpha = 0.8f)
                                    )
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

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                // Favorite Star Button
                                IconButton(
                                    onClick = {
                                        val index = localTasks.indexOfFirst { it.id == task.id }
                                        if (index != -1) {
                                            localTasks[index] = task.withFavorite(!task.isFavorite)
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (task.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                        contentDescription = "Favorito",
                                        tint = if (task.isFavorite) Color(0xFFFFB74D) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Status Selector Dropdown for Local Task row
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
                                                    val index = localTasks.indexOfFirst { it.id == task.id }
                                                    if (index != -1) {
                                                        localTasks[index] = task.withStatus(st)
                                                    }
                                                    showStatusDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Delete Local Task Button
                                IconButton(
                                    onClick = {
                                        localTasks.removeIf { it.id == task.id }
                                    },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .testTag("local_task_delete_${task.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Remover tarefa local",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
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
                val todoTasks = localTasks.filter { it.status == TaskStatus.TO_DO }
                PipelineLane(
                    title = "A Fazer",
                    count = todoTasks.size,
                    tasks = todoTasks,
                    accentColor = Color(0xFF90A4AE),
                    actionButtonText = "Começar Trabalho",
                    onActionClick = { task ->
                        val index = localTasks.indexOfFirst { it.id == task.id }
                        if (index != -1) {
                            localTasks[index] = task.copy(status = TaskStatus.IN_PROGRESS)
                        }
                    },
                    onDeleteClick = { task ->
                        localTasks.removeIf { it.id == task.id }
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
                val inProgressTasks = localTasks.filter { it.status == TaskStatus.IN_PROGRESS }
                PipelineLane(
                    title = "Em andamento",
                    count = inProgressTasks.size,
                    tasks = inProgressTasks,
                    accentColor = MaterialTheme.colorScheme.primary,
                    actionButtonText = "Marcar Concluído",
                    onActionClick = { task ->
                        val index = localTasks.indexOfFirst { it.id == task.id }
                        if (index != -1) {
                            localTasks[index] = task.copy(status = TaskStatus.COMPLETED, isCompleted = true)
                        }
                    },
                    onDeleteClick = { task ->
                        localTasks.removeIf { it.id == task.id }
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
                val completedTasks = localTasks.filter { it.status == TaskStatus.COMPLETED }
                PipelineLane(
                    title = "Concluído",
                    count = completedTasks.size,
                    tasks = completedTasks,
                    accentColor = Color(0xFF81C784),
                    actionButtonText = "Mover para Fazer",
                    onActionClick = { task ->
                        val index = localTasks.indexOfFirst { it.id == task.id }
                        if (index != -1) {
                            localTasks[index] = task.copy(status = TaskStatus.TO_DO, isCompleted = false)
                        }
                    },
                    onDeleteClick = { task ->
                        localTasks.removeIf { it.id == task.id }
                    }
                )
            }
        }
    }
}

@Composable
fun PipelineLane(
    title: String,
    count: Int,
    tasks: List<TaskItem>,
    accentColor: Color,
    actionButtonText: String,
    onActionClick: (TaskItem) -> Unit,
    onDeleteClick: (TaskItem) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = accentColor
                    )
                }
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sem tarefas nesta etapa",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            } else {
                tasks.forEach { task ->
                    val priorityColor = when (task.priority) {
                        TaskPriority.HIGH -> Color(0xFFEF5350)
                        TaskPriority.MEDIUM -> Color(0xFFFFB74D)
                        TaskPriority.LOW -> Color(0xFF81C784)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                                    ),
                                    color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = task.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = "•",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                    )
                                    Text(
                                        text = task.priority.name,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = priorityColor.copy(alpha = 0.8f)
                                    )
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

                            IconButton(
                                onClick = { onDeleteClick(task) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Remover",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Custom transition action button for Kanban flow
                        Button(
                            onClick = { onActionClick(task) },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentColor.copy(alpha = 0.12f),
                                contentColor = accentColor
                            ),
                            elevation = null
                        ) {
                            Text(
                                text = actionButtonText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }
    }
}

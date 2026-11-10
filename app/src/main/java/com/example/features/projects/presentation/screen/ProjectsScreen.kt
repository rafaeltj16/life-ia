package com.example.features.projects.presentation.screen

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
import com.example.core.ui.*
import com.example.core.common.*
import com.example.core.common.LifeOSViewModel

@Composable
fun ProjectsScreen(viewModel: LifeOSViewModel) {
    val projects by viewModel.projects.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Project?>(null) }
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#7E57C2") }

    val colorPalette = listOf(
        "#7E57C2" to "Roxo (Lavender)",
        "#29B6F6" to "Azul (Azure)",
        "#FFA726" to "Laranja (Amber)",
        "#81C784" to "Verde (Emerald)",
        "#EF5350" to "Vermelho (Coral)",
        "#EC407A" to "Rosa (Magenta)",
        "#008080" to "Teal",
        "#795548" to "Marrom (Moka)"
    )

    ResponsiveScreenContainer {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Meus Projetos", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = "Acompanhe e categorize suas metas",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }

                LifeOSButton(
                    text = "Novo Projeto",
                    onClick = {
                        name = ""
                        selectedColor = "#7E57C2"
                        showAddDialog = true
                    },
                    icon = Icons.Default.Add,
                    modifier = Modifier.testTag("add_project_button")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (projects.isEmpty()) {
                EmptyStateView(
                    title = "Nenhum projeto cadastrado",
                    description = "Crie seu primeiro projeto para começar a organizar suas tarefas e acompanhar o progresso diário."
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(projects, key = { it.id }) { project ->
                        val projectTasks = tasks.filter { it.projectId == project.id }
                        val completed = projectTasks.count { it.isCompleted }
                        val total = projectTasks.size
                        val progress = if (total > 0) completed.toFloat() / total else 0f

                        var isExpanded by remember { mutableStateOf(false) }
                        var showAddTaskInline by remember { mutableStateOf(false) }
                        var inlineTaskTitle by remember { mutableStateOf("") }

                        LifeOSCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Color Tag Badge Indicator
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .clip(CircleShape)
                                            .background(Color(android.graphics.Color.parseColor(project.color)))
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = project.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (total > 0) "$completed de $total tarefas concluídas" else "Nenhuma tarefa vinculada",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            showEditDialog = project
                                            name = project.name
                                            selectedColor = project.color
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar Projeto",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteProject(project.id) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remover Projeto",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { isExpanded = !isExpanded },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = "Expandir",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Progresso Geral: ${(progress * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape),
                                    color = Color(android.graphics.Color.parseColor(project.color)),
                                    trackColor = Color(android.graphics.Color.parseColor(project.color)).copy(alpha = 0.15f)
                                )

                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)))
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "Tarefas do Projeto",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    if (projectTasks.isEmpty()) {
                                        Text(
                                            text = "Nenhuma tarefa cadastrada neste projeto.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )
                                    } else {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            projectTasks.forEach { task ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(
                                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Checkbox(
                                                        checked = task.isCompleted,
                                                        onCheckedChange = { viewModel.toggleTask(task.id) },
                                                        colors = CheckboxDefaults.colors(checkedColor = Color(android.graphics.Color.parseColor(project.color)))
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = task.title,
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                                                            fontWeight = FontWeight.Medium
                                                        ),
                                                        color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (showAddTaskInline) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Box(modifier = Modifier.weight(1f)) {
                                                LifeOSInput(
                                                    value = inlineTaskTitle,
                                                    onValueChange = { inlineTaskTitle = it },
                                                    placeholder = "Nova tarefa..."
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    if (inlineTaskTitle.isNotBlank()) {
                                                        viewModel.addTask(
                                                            title = inlineTaskTitle,
                                                            category = "Projeto",
                                                            priority = TaskPriority.MEDIUM,
                                                            projectId = project.id
                                                        )
                                                        inlineTaskTitle = ""
                                                        showAddTaskInline = false
                                                    }
                                                }
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Salvar", tint = Color(android.graphics.Color.parseColor(project.color)))
                                            }
                                            IconButton(
                                                onClick = { showAddTaskInline = false }
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Cancelar", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    } else {
                                        TextButton(
                                            onClick = { showAddTaskInline = true },
                                            colors = ButtonDefaults.textButtonColors(contentColor = Color(android.graphics.Color.parseColor(project.color)))
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Adicionar tarefa rápida", style = MaterialTheme.typography.labelLarge)
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

    if (showAddDialog) {
        LifeOSDialog(
            title = "Criar Novo Projeto",
            onDismissRequest = { showAddDialog = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addProject(name, selectedColor)
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Criar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
            }
        ) {
            LifeOSInput(value = name, onValueChange = { name = it }, placeholder = "Nome do Projeto (ex: Reforma da Casa)")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecione uma cor para o Projeto", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colorPalette.forEach { (colorCode, label) ->
                    val isSel = selectedColor == colorCode
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(colorCode)))
                            .border(
                                width = if (isSel) 3.dp else 1.dp,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = colorCode }
                    )
                }
            }
        }
    }

    if (showEditDialog != null) {
        val editingProj = showEditDialog!!
        LifeOSDialog(
            title = "Editar Projeto",
            onDismissRequest = { showEditDialog = null },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.updateProject(editingProj.id, name, selectedColor)
                            showEditDialog = null
                        }
                    }
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = null }) { Text("Cancelar") }
            }
        ) {
            LifeOSInput(value = name, onValueChange = { name = it }, placeholder = "Nome do Projeto")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Selecione uma cor", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colorPalette.forEach { (colorCode, label) ->
                    val isSel = selectedColor == colorCode
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(android.graphics.Color.parseColor(colorCode)))
                            .border(
                                width = if (isSel) 3.dp else 1.dp,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimary else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { selectedColor = colorCode }
                    )
                }
            }
        }
    }
}

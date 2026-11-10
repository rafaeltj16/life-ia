package com.lifeos.features.habits.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.example.core.common.GoalItem
import com.example.core.common.LifeOSViewModel
import com.example.core.ui.LifeOSCard
import com.example.core.ui.ResponsiveScreenContainer

@Composable
fun GoalsScreen(
    viewModel: LifeOSViewModel? = null
) {
    val liveGoals = viewModel?.goals?.collectAsState()?.value ?: listOf(
        GoalItem(title = "🏃 Maratona de Berlim (42K)", targetValue = "42 km", progressValue = "25 km", progressPercent = 0.59f),
        GoalItem(title = "📚 Ler 24 livros no ano", targetValue = "24 livros", progressValue = "12 livros", progressPercent = 0.50f),
        GoalItem(title = "💰 Reserva de Emergência", targetValue = "R$ 30.000", progressValue = "R$ 15.000", progressPercent = 0.50f)
    )

    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newTarget by remember { mutableStateOf("") }

    ResponsiveScreenContainer {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_goal_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Meta")
                }
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "🎯 Metas de Longo Prazo",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Acompanhamento estratégico alinhado ao motor de IA",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // AI Goal Alignment Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Alinhamento Inteligente do LifeOS",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "O LifeOS AI ajusta seus blocos de foco e hábitos semanais automaticamente para apoiar o avanço destas metas.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                items(liveGoals, key = { it.id }) { goal ->
                    GoalItemCard(
                        title = goal.title,
                        value = "${goal.progressValue} / ${goal.targetValue}",
                        progress = goal.progressPercent,
                        onIncrement = {
                            val newPercent = (goal.progressPercent + 0.05f).coerceAtMost(1.0f)
                            viewModel?.updateGoalProgress(goal.id, "${(newPercent * 100).toInt()}%", newPercent)
                        }
                    )
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nova Meta Estratégica") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Título da Meta (ex: 🏃 Meia Maratona)") },
                            modifier = Modifier.fillMaxWidth().testTag("new_goal_title")
                        )
                        OutlinedTextField(
                            value = newTarget,
                            onValueChange = { newTarget = it },
                            label = { Text("Objetivo Final (ex: 21 km ou 10 livros)") },
                            modifier = Modifier.fillMaxWidth().testTag("new_goal_target")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTitle.isNotBlank() && newTarget.isNotBlank()) {
                                viewModel?.addGoal(
                                    title = newTitle,
                                    targetValue = newTarget,
                                    progressValue = "0",
                                    progressPercent = 0.0f
                                )
                                newTitle = ""
                                newTarget = ""
                                showAddDialog = false
                            }
                        },
                        modifier = Modifier.testTag("save_new_goal_button")
                    ) {
                        Text("Criar Meta")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun GoalItemCard(
    title: String,
    value: String,
    progress: Float,
    onIncrement: (() -> Unit)? = null
) {
    LifeOSCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            if (onIncrement != null) {
                FilledTonalButton(
                    onClick = onIncrement,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("increment_goal_progress")
                ) {
                    Text("+5%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Progresso da Meta", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    }
}

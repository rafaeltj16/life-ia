package com.lifeos.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CopilotPipelineStage(
    val id: Int,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun LifeCopilotGoalPlannerWidget(
    modifier: Modifier = Modifier,
    onPlanExecuted: () -> Unit = {}
) {
    var userGoalPrompt by remember { mutableStateOf("Quero lançar meu aplicativo em 90 dias.") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var currentStageIndex by remember { mutableIntStateOf(-1) }
    var planCompleted by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val stages = remember {
        listOf(
            CopilotPipelineStage(0, "Projeto", "Definição de escopo e objetivos do novo projeto", Icons.Default.FolderSpecial, Color(0xFF7E57C2)),
            CopilotPipelineStage(1, "Dashboard Premium", "Métricas personalizadas e visualização consolidada", Icons.Default.Dashboard, Color(0xFF1E88E5)),
            CopilotPipelineStage(2, "15 tarefas", "Decomposição em 15 tarefas operacionais prioritárias", Icons.Default.TaskAlt, Color(0xFF00897B)),
            CopilotPipelineStage(3, "3 hábitos", "3 rotinas recorrentes integradas ao seu rastreador", Icons.Default.Repeat, Color(0xFF43A047)),
            CopilotPipelineStage(4, "10 sessões de foco", "10 blocos Pomodoro programados no Smart Timeline", Icons.Default.Timer, Color(0xFFFB8C00)),
            CopilotPipelineStage(5, "Prazo sugerido", "Prazo otimizado pela IA: 90 dias (Lançamento garantido)", Icons.Default.Schedule, Color(0xFFD81B60))
        )
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF7E57C2).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Life Copilot",
                            tint = Color(0xFF512DA8),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Life Copilot • Planejador Automático",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Transforme metas em planos de ação completos",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEDE7F6),
                    contentColor = Color(0xFF512DA8)
                ) {
                    Text(
                        text = "AI Autonomous",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User Prompt Input
            OutlinedTextField(
                value = userGoalPrompt,
                onValueChange = { userGoalPrompt = it },
                label = { Text("Qual é a sua grande meta?") },
                placeholder = { Text("Ex: Quero lançar meu aplicativo em 90 dias.") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (userGoalPrompt.isNotBlank() && !isAnalyzing) {
                                isAnalyzing = true
                                planCompleted = false
                                currentStageIndex = 0

                                scope.launch {
                                    for (i in stages.indices) {
                                        currentStageIndex = i
                                        delay(800)
                                    }
                                    isAnalyzing = false
                                    planCompleted = true
                                    onPlanExecuted()
                                }
                            }
                        },
                        enabled = userGoalPrompt.isNotBlank() && !isAnalyzing
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Executar Planejamento Copilot",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            // Suggestion chip shortcut
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        userGoalPrompt = "Quero lançar meu aplicativo em 90 dias."
                    }
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Exemplo: \"Quero lançar meu aplicativo em 90 dias.\"",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Pipeline Progress Visualizer
            if (currentStageIndex >= 0) {
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (planCompleted) "Planejamento Concluído com Sucesso!" else "Life Copilot Executando Pipeline...",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (planCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${currentStageIndex + 1}/${stages.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { ((currentStageIndex + 1).toFloat() / stages.size.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stages List
                    stages.forEachIndexed { idx, stage ->
                        val isDone = idx < currentStageIndex || planCompleted
                        val isCurrent = idx == currentStageIndex && !planCompleted

                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isDone -> Color(0xFF2E7D32)
                                                isCurrent -> stage.accentColor
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else stage.icon,
                                        contentDescription = null,
                                        tint = if (isDone || isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stage.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isCurrent || isDone) FontWeight.Bold else FontWeight.Medium,
                                        color = when {
                                            isDone -> Color(0xFF2E7D32)
                                            isCurrent -> stage.accentColor
                                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                                        }
                                    )
                                    if (isCurrent || isDone) {
                                        Text(
                                            text = stage.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (idx < stages.size - 1) {
                                Row(
                                    modifier = Modifier.padding(start = 7.dp, top = 2.dp, bottom = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = if (idx < currentStageIndex || planCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outlineVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = planCompleted,
                        enter = fadeIn() + expandVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "🚀 Projeto Criado: 'Lançamento App 2026'\n📊 Dashboard Premium vinculado\n✅ 15 Tarefas geradas no Kanban\n🔄 3 Hábitos configurados no rastreador\n⏱️ 10 Sessões de Foco programadas\n📅 Prazo Sugerido: 90 Dias",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

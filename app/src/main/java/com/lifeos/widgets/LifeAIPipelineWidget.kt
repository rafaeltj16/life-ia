package com.lifeos.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.unit.sp
import com.lifeos.core.workflow.LifeAI
import com.lifeos.core.workflow.LifeAIPipelineResult
import com.lifeos.core.workflow.PipelineStage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PipelineStepMeta(
    val stage: PipelineStage,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun LifeAIPipelineWidget(
    modifier: Modifier = Modifier,
    lifeAI: LifeAI = remember { LifeAI() }
) {
    val scope = rememberCoroutineScope()
    var inputMessage by remember { mutableStateOf("Planejamento matinal com IA e sincronizar dados") }
    var isExecuting by remember { mutableStateOf(false) }
    var pipelineResult by remember { mutableStateOf<LifeAIPipelineResult?>(null) }
    var activeStepIndex by remember { mutableIntStateOf(-1) }
    var selectedTab by remember { mutableIntStateOf(0) }

    val pipelineSteps = remember {
        listOf(
            PipelineStepMeta(
                stage = PipelineStage.MENSAGEM,
                title = "Mensagem",
                subtitle = "Input do Usuário",
                icon = Icons.Default.Message,
                color = Color(0xFF1E88E5)
            ),
            PipelineStepMeta(
                stage = PipelineStage.LIFE_AI,
                title = "LifeAI",
                subtitle = "Orquestrador Central",
                icon = Icons.Default.AutoAwesome,
                color = Color(0xFF7E57C2)
            ),
            PipelineStepMeta(
                stage = PipelineStage.INTENT_DETECTOR,
                title = "Intent Detector",
                subtitle = "Identificação de Intenção",
                icon = Icons.Default.Psychology,
                color = Color(0xFF00897B)
            ),
            PipelineStepMeta(
                stage = PipelineStage.WORKFLOW_PARSER,
                title = "Workflow Parser",
                subtitle = "Extração de Parâmetros",
                icon = Icons.Default.Code,
                color = Color(0xFFE65100)
            ),
            PipelineStepMeta(
                stage = PipelineStage.WORKFLOW_BUILDER,
                title = "Workflow Builder",
                subtitle = "Construção do Objeto",
                icon = Icons.Default.AccountTree,
                color = Color(0xFF0288D1)
            ),
            PipelineStepMeta(
                stage = PipelineStage.WORKFLOW_VALIDATOR,
                title = "Workflow Validator",
                subtitle = "Validação de Regras",
                icon = Icons.Default.VerifiedUser,
                color = Color(0xFF43A047)
            ),
            PipelineStepMeta(
                stage = PipelineStage.WORKFLOW_EXECUTOR,
                title = "Workflow Executor",
                subtitle = "Execução de Ações",
                icon = Icons.Default.RocketLaunch,
                color = Color(0xFFD81B60)
            )
        )
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "LifeAI Pipeline",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Pipeline LifeAI de Workflows",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Mensagem → LifeAI → Intent Detector → Parser → Builder → Validator → Executor",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick suggestion chips
            Text(
                text = "Sugestões de Mensagens / Fluxos para Testar:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SuggestionChip(
                    onClick = { inputMessage = "TaskCompleted → StartFocus → Atualizar Dashboard → Recalcular Score" },
                    label = { Text("⚡ Task → Foco → Dashboard → Score", fontSize = 11.sp) }
                )
                SuggestionChip(
                    onClick = { inputMessage = "Planejamento matinal com IA e sincronizar dados" },
                    label = { Text("🌅 Matinal", fontSize = 11.sp) }
                )
                SuggestionChip(
                    onClick = { inputMessage = "Iniciar sessão de foco imersivo de 45 min" },
                    label = { Text("🎯 Foco", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input TextField
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                label = { Text("Mensagem (Prompt do Usuário)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 2,
                trailingIcon = {
                    Button(
                        onClick = {
                            if (!isExecuting && inputMessage.isNotBlank()) {
                                scope.launch {
                                    isExecuting = true
                                    pipelineResult = null
                                    activeStepIndex = 0

                                    // Visual progressive stepping animation
                                    for (i in 0 until pipelineSteps.size) {
                                        activeStepIndex = i
                                        delay(120)
                                    }

                                    val res = lifeAI.processMessage(inputMessage)
                                    pipelineResult = res
                                    activeStepIndex = pipelineSteps.size - 1
                                    isExecuting = false
                                }
                            }
                        },
                        enabled = !isExecuting && inputMessage.isNotBlank(),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 4.dp),
                        contentPadding = ButtonDefaults.TextButtonContentPadding
                    ) {
                        if (isExecuting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Executar",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Executar", fontSize = 12.sp)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Pipeline Visual Progression (Vertical or Horizontal Step Nodes)
            Text(
                text = "Fluxo de Execução do Pipeline:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                pipelineSteps.forEachIndexed { index, step ->
                    val isPast = pipelineResult != null || activeStepIndex > index
                    val isCurrent = isExecuting && activeStepIndex == index
                    val isUpcoming = !isPast && !isCurrent

                    val nodeBg by animateColorAsState(
                        targetValue = when {
                            isPast -> step.color
                            isCurrent -> step.color.copy(alpha = 0.8f)
                            else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        },
                        animationSpec = tween(300),
                        label = "nodeBg"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        // Icon node
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(nodeBg),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCurrent) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else if (isPast) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Concluído",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = step.icon,
                                    contentDescription = step.title,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${index + 1}. ${step.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isPast || isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (isPast || isCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = step.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isPast) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = step.color.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "OK",
                                    color = step.color,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    if (index < pipelineSteps.size - 1) {
                        Box(
                            modifier = Modifier
                                .padding(start = 13.dp)
                                .width(2.dp)
                                .height(8.dp)
                                .background(if (isPast) step.color.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        )
                    }
                }
            }

            // Results details card
            AnimatedVisibility(visible = pipelineResult != null) {
                pipelineResult?.let { res ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Sucesso",
                                            tint = Color(0xFF43A047),
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Pipeline Concluído com Sucesso!",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                    Text(
                                        text = "${res.totalTimeMs}ms",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Workflow Gerado: ${res.builtWorkflow.name} (${res.builtWorkflow.actions.size} ações)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Intenção: ${res.detectedIntent.intentName} | Categoria: ${res.builtWorkflow.category}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Gatilho: ${res.builtWorkflow.trigger.javaClass.simpleName}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

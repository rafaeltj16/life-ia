package com.lifeos.widgets

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

data class WorkflowStepItem(
    val stepIndex: Int,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    var isCompleted: Boolean = false
)

@Composable
fun ProjectWorkflowWidget(
    modifier: Modifier = Modifier,
    projectName: String = "Lançamento do App LifeOS",
    onStartFocusSession: () -> Unit = {}
) {
    var activeStepIndex by remember { mutableIntStateOf(0) }
    var noteText by remember { mutableStateOf("Estruturar proposta de valor, CTA principal e captura de leads.") }
    var copyText by remember { mutableStateOf("Transforme sua rotina com o LifeOS: Produtividade inteligente com IA.") }
    
    val checklistItems = remember {
        mutableStateListOf(
            "Validar headline da Landing Page" to true,
            "Revisar cópia das chamadas para ação (CTA)" to false,
            "Configurar formulário de cadastro de e-mail" to false,
            "Testar fluxo no dispositivo móvel" to false
        )
    }

    val steps = remember {
        listOf(
            WorkflowStepItem(0, "Projeto", "Definição do escopo e objetivos", Icons.Default.FolderSpecial, Color(0xFF7E57C2)),
            WorkflowStepItem(1, "Landing Page", "Design de estrutura e captura", Icons.Default.Web, Color(0xFF1E88E5)),
            WorkflowStepItem(2, "Nota", "Anotações e insights estratégicos", Icons.Default.EditNote, Color(0xFF00897B)),
            WorkflowStepItem(3, "Copy", "Redação persuasiva e CTAs", Icons.Default.Description, Color(0xFFE65100)),
            WorkflowStepItem(4, "Checklist", "Tarefas de validação e lançamento", Icons.Default.FormatListNumbered, Color(0xFF43A047)),
            WorkflowStepItem(5, "Sessão de Foco", "Execução concentrada de 25 min", Icons.Default.PlayArrow, Color(0xFFD81B60))
        )
    }

    ElevatedCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Title Header
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
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = "Fluxo do Projeto",
                            tint = Color(0xFF512DA8),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = projectName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Pipeline: Projeto ➔ Landing Page ➔ Nota ➔ Copy ➔ Checklist ➔ Foco",
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
                        text = "Etapa ${activeStepIndex + 1}/6",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Pipeline Visual Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Etapas da Execução",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                steps.forEachIndexed { index, step ->
                    val isActive = activeStepIndex == index
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isActive) step.color.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .clickable { activeStepIndex = index }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isActive) step.color else step.color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.icon,
                                contentDescription = null,
                                tint = if (isActive) Color.White else step.color,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${index + 1}. ${step.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                color = if (isActive) step.color else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = step.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (isActive) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = step.color,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (index < steps.size - 1) {
                        Row(
                            modifier = Modifier.padding(start = 20.dp, top = 2.dp, bottom = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Detail Box according to activeStepIndex
            val activeStep = steps[activeStepIndex]
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(activeStep.color.copy(alpha = 0.08f))
                    .border(1.dp, activeStep.color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = activeStep.icon,
                            contentDescription = null,
                            tint = activeStep.color,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Painel: ${activeStep.title}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = activeStep.color
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = activeStep.color.copy(alpha = 0.2f),
                        contentColor = activeStep.color
                    ) {
                        Text(
                            text = activeStep.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (activeStepIndex) {
                    0 -> { // Projeto
                        Text(
                            text = "Defina o objetivo central do seu projeto. O LifeOS guiará você pela criação da Landing Page, Notas, Copys e Checklist até o Foco de execução.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { activeStepIndex = 1 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Avançar para Landing Page ➔")
                        }
                    }
                    1 -> { // Landing Page
                        Text(
                            text = "Estrutura da Landing Page:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Hero Section: Titulo de alto impacto\n• Proposta de Valor: Benefícios do produto\n• Formulário de Captura de Leads\n• Prova Social & Depoimentos",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { activeStepIndex = 2 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Avançar para Nota ➔")
                        }
                    }
                    2 -> { // Nota
                        Text(
                            text = "Notas do Projeto:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodySmall,
                            placeholder = { Text("Escreva suas anotações estratégicas aqui...") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { activeStepIndex = 3 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Avançar para Copy ➔")
                        }
                    }
                    3 -> { // Copy
                        Text(
                            text = "Texto Persuasivo (Copy):",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = copyText,
                            onValueChange = { copyText = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.bodySmall,
                            placeholder = { Text("Crie os textos e CTAs da sua campanha...") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { activeStepIndex = 4 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Avançar para Checklist ➔")
                        }
                    }
                    4 -> { // Checklist
                        Text(
                            text = "Checklist de Validação:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        checklistItems.forEachIndexed { idx, item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        checklistItems[idx] = item.first to !item.second
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (item.second) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (item.second) activeStep.color else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.first,
                                    style = MaterialTheme.typography.bodySmall,
                                    textDecoration = if (item.second) TextDecoration.LineThrough else TextDecoration.None
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { activeStepIndex = 5 },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Avançar para Sessão de Foco ➔")
                        }
                    }
                    5 -> { // Sessão de Foco
                        Text(
                            text = "Pronto para executar! Inicie uma sessão de foco para concluir as tarefas pendentes deste projeto.",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onStartFocusSession,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Iniciar Sessão de Foco (25 min)")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Task Completion Event Bus Propagation Flow
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFECEFF1))
                    .border(1.dp, Color(0xFFCFD8DC), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF37474F),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Barramento de Eventos de Tarefa",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF263238)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF455A64),
                        contentColor = Color.White
                    ) {
                        Text(
                            text = "Event Bus Flow",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val eventBusNodes = listOf(
                    "Task Completed" to Color(0xFF4CAF50),
                    "Event Bus" to Color(0xFF00ACC1),
                    "Atualizar Projeto" to Color(0xFF1E88E5),
                    "Atualizar Objetivo" to Color(0xFF7E57C2),
                    "Atualizar Life Score" to Color(0xFFD81B60),
                    "Atualizar Dashboard" to Color(0xFF3F51B5),
                    "Gerar XP" to Color(0xFFFFB300),
                    "Atualizar Analytics" to Color(0xFF009688),
                    "Verificar Conquistas" to Color(0xFF8E24AA),
                    "Enviar para IA" to Color(0xFFE65100)
                )

                eventBusNodes.forEachIndexed { idx, (label, color) ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = color.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${idx + 1}. $label",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = color
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    if (idx < eventBusNodes.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

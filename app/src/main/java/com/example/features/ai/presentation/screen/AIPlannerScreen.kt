package com.example.features.ai.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.ai.repository.AIPlanItem
import com.example.features.ai.presentation.viewmodel.LifeAIViewModel
import com.example.core.common.LifeOSViewModel

@Composable
fun LifeAIBriefingCard() {
    var isAcademiaCompleted by remember { mutableStateOf(true) }
    var isDashboardCompleted by remember { mutableStateOf(false) }
    var isLandingPageCompleted by remember { mutableStateOf(false) }
    var isAguaCompleted by remember { mutableStateOf(false) }
    var isLeituraCompleted by remember { mutableStateOf(false) }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: ☀ Bom dia Rafael
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = "☀ Bom dia Rafael",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                )
            }
            
            Text(
                text = "Hoje você tem:",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Badges
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎯", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "3 prioridades",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "Sequência de 27 dias",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⭐", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = "Você ganhou 120 XP ontem",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            
            // Divider
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            // 🤖 LifeAI Section
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "🤖 LifeAI",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    )
                )
            }
            
            Text(
                text = "Bom dia!",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            
            Text(
                text = "Hoje seu horário de maior foco será entre 08:30 e 11:00.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            Text(
                text = "Sugiro começar pelo Projeto LifeOS.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Divider
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(vertical = 4.dp)
            )
            
            // Plano do dia Section
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Plano do dia",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Interactive checklist items
            val checklistItems = listOf(
                Triple("Academia", isAcademiaCompleted) { isAcademiaCompleted = !isAcademiaCompleted },
                Triple("Dashboard", isDashboardCompleted) { isDashboardCompleted = !isDashboardCompleted },
                Triple("Landing Page", isLandingPageCompleted) { isLandingPageCompleted = !isLandingPageCompleted },
                Triple("Água", isAguaCompleted) { isAguaCompleted = !isAguaCompleted },
                Triple("Leitura", isLeituraCompleted) { isLeituraCompleted = !isLeituraCompleted }
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                checklistItems.forEach { (name, completed, onToggle) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle() }
                            .padding(vertical = 4.dp)
                    ) {
                        if (completed) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Concluído",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (completed) FontWeight.Normal else FontWeight.SemiBold,
                                color = if (completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (completed) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MissaoDoDiaCard() {
    var isStarted by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.tertiary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Missão Principal
            Text(
                text = "Missão Principal",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mission Name
            Text(
                text = "Concluir Landing Page",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Tempo estimado
            Text(
                text = "Tempo estimado",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "1h40",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Impacto
            Text(
                text = "Impacto",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "★★★★★",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color(0xFFFFB300),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Button Começar
            Button(
                onClick = { isStarted = !isStarted },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isStarted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = if (isStarted) "Em Progresso" else "Começar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

data class PipelineStep(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Composable
fun AIPipelineFlowCard() {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Orquestrador de IA: Pipeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Recolher" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Entenda como o LifeOS constrói sua agenda inteligente a partir de seus dados (Toque para ver).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 4.dp, end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val pipelineSteps = listOf(
                        PipelineStep(
                            title = "Usuário",
                            description = "Início da jornada: suas metas, preferências pessoais e rotina diária.",
                            icon = Icons.Default.Person,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        PipelineStep(
                            title = "Dashboard",
                            description = "Consolidação visual em tempo real de seus hábitos, tarefas, projetos e agenda.",
                            icon = Icons.Default.Dashboard,
                            color = Color(0xFF9C27B0)
                        ),
                        PipelineStep(
                            title = "Copilot",
                            description = "Interação conversacional e diagnósticos proativos do assistente pessoal.",
                            icon = Icons.Default.Chat,
                            color = Color(0xFF3F51B5)
                        ),
                        PipelineStep(
                            title = "Rules Engine",
                            description = "Validação rigorosa de heurísticas de produtividade, limites de esforço e rotinas de saúde.",
                            icon = Icons.Default.ListAlt,
                            color = Color(0xFF00C853)
                        ),
                        PipelineStep(
                            title = "Prediction Engine",
                            description = "Cálculo matemático preditivo de probabilidade de conclusão, cansaço e melhores horários de foco.",
                            icon = Icons.Default.TrendingUp,
                            color = Color(0xFF00BCD4)
                        ),
                        PipelineStep(
                            title = "Gemini",
                            description = "Sintetização avançada de IA Generativa para dar o tom perfeito, categorização e conselhos contextuais.",
                            icon = Icons.Default.AutoAwesome,
                            color = Color(0xFFFFB300)
                        ),
                        PipelineStep(
                            title = "Plano Inteligente",
                            description = "A agenda perfeitamente otimizada, dinâmica e interativa gerada para maximizar seu rendimento.",
                            icon = Icons.Default.CalendarToday,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        PipelineStep(
                            title = "Usuário",
                            description = "Execução fluida pelo usuário, gerando novas métricas de progresso que fecham e reiniciam o ciclo contínuo.",
                            icon = Icons.Default.Autorenew,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    pipelineSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(36.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(step.color.copy(alpha = 0.12f), CircleShape)
                                        .border(1.dp, step.color.copy(alpha = 0.4f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = step.icon,
                                        contentDescription = null,
                                        tint = step.color,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                if (index < pipelineSteps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(32.dp)
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(step.color, pipelineSteps[index + 1].color)
                                                )
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(bottom = if (index < pipelineSteps.size - 1) 16.dp else 0.dp)
                            ) {
                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = step.description,
                                    style = MaterialTheme.typography.bodySmall,
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

@Composable
fun AIPlannerScreen(
    aiViewModel: LifeAIViewModel,
    mainViewModel: LifeOSViewModel
) {
    val dailyPlan by aiViewModel.dailyPlan.collectAsState()
    val isPlanLoading by aiViewModel.isPlanLoading.collectAsState()
    
    val habits by mainViewModel.habits.collectAsState()
    val tasks by mainViewModel.tasks.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. LifeAI Briefing Card (Requested visual layout)
        item {
            LifeAIBriefingCard()
        }

        // 2. Missão do Dia Card
        item {
            MissaoDoDiaCard()
        }

        // 3. AI Pipeline flow card
        item {
            AIPipelineFlowCard()
        }

        // 4. Welcome and explanation card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.EventNote,
                            contentDescription = "Planejador de Rotina com IA",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Planejador Diário Inteligente",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Crie uma grade horária customizada com base nas suas tarefas pendentes, hábitos ativos e memórias gravadas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (isPlanLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "O LifeOS AI está orquestrando seus horários...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else if (dailyPlan.isEmpty()) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Grade Horária Vazia",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Gere uma agenda sob medida clicando no botão abaixo.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    Button(
                        onClick = {
                            val habitsList = habits.map { it.name }
                            val tasksList = tasks.filter { !it.isCompleted }.map { it.title }
                            aiViewModel.generatePlan(habitsList, tasksList)
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp)
                            .testTag("generate_initial_plan_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Gerar Planejamento com IA")
                    }
                }
            }
        } else {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seu Cronograma Diário",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    
                    TextButton(
                        onClick = {
                            val habitsList = habits.map { it.name }
                            val tasksList = tasks.filter { !it.isCompleted }.map { it.title }
                            aiViewModel.generatePlan(habitsList, tasksList)
                        },
                        modifier = Modifier.testTag("regenerate_plan_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Recalcular")
                    }
                }
            }

            items(dailyPlan) { item ->
                PlanItemTimelineCard(
                    item = item,
                    onAddToCalendar = {
                        mainViewModel.addEvent(
                            title = item.activity,
                            time = item.time,
                            category = item.category,
                            description = "Adicionado via recomendação inteligente de rotina do LifeOS AI."
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun PlanItemTimelineCard(item: AIPlanItem, onAddToCalendar: () -> Unit) {
    var isAdded by remember { mutableStateOf(false) }

    val priorityColor = when (item.priority.lowercase()) {
        "alta", "high" -> MaterialTheme.colorScheme.error
        "média", "media", "medium" -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.secondary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Time bubble on left
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(55.dp)
        ) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(30.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Activity card on right
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(item.icon, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.activity,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Category pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            // Priority pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(priorityColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.priority,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = priorityColor
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        if (!isAdded) {
                            onAddToCalendar()
                            isAdded = true
                        }
                    },
                    modifier = Modifier.testTag("add_plan_item_to_calendar_${item.id}"),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (isAdded) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isAdded) "Adicionado à agenda" else "Adicionar à agenda",
                        tint = if (isAdded) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

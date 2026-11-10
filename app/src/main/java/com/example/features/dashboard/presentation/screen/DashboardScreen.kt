package com.example.features.dashboard.presentation.screen

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.AuthState
import com.example.core.common.NavigationModule
import com.example.core.common.DashboardWidget
import com.example.core.ui.LifeOSCard
import com.example.features.dashboard.presentation.components.*
import com.example.features.dashboard.presentation.viewmodel.DashboardViewModel
import com.example.features.dashboard.presentation.state.DashboardUiState
import com.example.features.dashboard.presentation.event.DashboardEvent
import androidx.compose.ui.platform.LocalContext
import com.example.services.sync.SyncQueueProcessor
import com.lifeos.widgets.FinancialWidget
import com.lifeos.widgets.HealthWidget as LifeOSHealthWidget
import com.lifeos.widgets.SmartTimelineWidget
import com.lifeos.widgets.FocusAnalyticsWidget
import com.lifeos.widgets.ProjectWorkflowWidget
import com.lifeos.widgets.LifeAIPipelineWidget
import com.lifeos.widgets.KnowledgeWidget
import com.lifeos.widgets.LifeCopilotGoalPlannerWidget
import com.lifeos.widgets.ExecutiveSummaryWidget
import com.lifeos.widgets.LifeCopilotQuickWidget
import com.lifeos.widgets.LifeGraphEngineWidget
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    onNavigateToCopilot: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val isEditingDashboard by viewModel.isEditingDashboard.collectAsState()
    val widgets by viewModel.lifeOSViewModel.widgets.collectAsState()
    val waterDrankMl by viewModel.lifeOSViewModel.waterDrankMl.collectAsState()
    val sleepHours by viewModel.lifeOSViewModel.sleepHours.collectAsState()
    val transactions by viewModel.lifeOSViewModel.transactions.collectAsState()
    val authState by viewModel.lifeOSViewModel.authState.collectAsState()
    var showDetails by remember { mutableStateOf(true) }

    val userName = when (val auth = authState) {
        is AuthState.Authenticated -> auth.name
        else -> "Usuário"
    }

    // Determine Greeting based on time
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour in 5..12 -> "Bom dia"
        hour in 13..18 -> "Boa tarde"
        else -> "Boa noite"
    }

    val df = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    val currentDateStr = df.format(Date()).replaceFirstChar { it.uppercase() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when (val uiState = state) {
            is DashboardUiState.Loading -> {
                LoadingCard()
            }
            is DashboardUiState.Error -> {
                ErrorCard(
                    message = uiState.message,
                    onRetry = { viewModel.onEvent(DashboardEvent.Retry) }
                )
            }
            is DashboardUiState.Success -> {
                val dashboard = uiState.dashboard
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header: Greeting, Date, and Widget Customizer toggle button
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GreetingCard(
                                userName = userName,
                                greeting = greeting,
                                currentDateStr = currentDateStr,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            // Minimal Toggle to customize existing widgets
                            IconButton(
                                onClick = { viewModel.toggleEditDashboard(!isEditingDashboard) },
                                modifier = Modifier
                                    .background(
                                        if (isEditingDashboard) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .size(44.dp)
                                    .testTag("toggle_dashboard_edit_mode")
                            ) {
                                Icon(
                                    imageVector = if (isEditingDashboard) Icons.Default.Check else Icons.Default.Settings,
                                    contentDescription = "Personalizar Módulos",
                                    tint = if (isEditingDashboard) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Sync Status Indicator representing "Room ➔ Sync Queue ➔ Supabase"
                    item {
                        val context = LocalContext.current
                        val syncProcessor = remember { SyncQueueProcessor.getInstance(context) }
                        val isSyncing by syncProcessor.isSyncing.collectAsState()
                        val syncStatus by syncProcessor.syncStatus.collectAsState()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSyncing) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                                )
                                .clickable { syncProcessor.processQueue() }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .testTag("sync_status_bar"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (syncStatus?.contains("sucesso") == true || syncStatus == "Sincronizado") Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                        contentDescription = "Status de Sincronização",
                                        tint = if (syncStatus?.contains("sucesso") == true || syncStatus == "Sincronizado") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Text(
                                    text = syncStatus ?: "Nuvem integrada",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Room ➔ Queue ➔ Supabase",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                                if (!isSyncing) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Sincronizar agora",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Edit Widgets panel if customized
                    if (isEditingDashboard) {
                        item {
                            LifeOSCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .widthIn(max = 640.dp)
                            ) {
                                Text(
                                    text = "⚙️ Personalizar Widgets",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Ative, desative ou reordene as seções da sua área de trabalho como desejar.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    widgets.sortedBy { it.order }.forEach { widget ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(
                                                    checked = widget.isVisible,
                                                    onCheckedChange = { viewModel.lifeOSViewModel.toggleWidgetVisibility(widget.id) }
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = widget.title,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (widget.isVisible) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                                    )
                                                )
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                IconButton(onClick = { viewModel.moveWidgetUp(widget.id) }) {
                                                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Subir widget")
                                                }
                                                IconButton(onClick = { viewModel.moveWidgetDown(widget.id) }) {
                                                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Descer widget")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Determine Level and XP dynamically
                        val completedTasks = dashboard.todayTasks.count { it.isCompleted }
                        val completedHabits = dashboard.todayHabits.count { it.isCompletedToday }
                        val totalXP = dashboard.xp
                        val level = dashboard.level
                        val projectProgress = 60 + (completedTasks * 4) + (completedHabits * 4)

                        val rawSummary = dashboard.aiInsight.summary
                        val aiRecommendation = when {
                            rawSummary.contains("Minha recomendação:") -> {
                                rawSummary.substringAfter("Minha recomendação:").trim()
                            }
                            rawSummary.contains("recomendação:") -> {
                                rawSummary.substringAfter("recomendação:").trim()
                            }
                            rawSummary.isNotBlank() -> {
                                rawSummary.trim()
                            }
                            else -> "Hoje recomendo focar nas tarefas do Projeto X."
                        }

                        val firstEvent = dashboard.events.firstOrNull()
                        val eventTime = firstEvent?.time?.substringBefore(" -") ?: "Hoje às 18:00"
                        val eventTitle = firstEvent?.title ?: "Treino"

                        val aguaHabit = dashboard.todayHabits.find { it.name.contains("água", ignoreCase = true) || it.name.contains("agua", ignoreCase = true) }
                        val meditacaoHabit = dashboard.todayHabits.find { it.name.contains("medit", ignoreCase = true) }
                        val leituraHabit = dashboard.todayHabits.find { it.name.contains("leit", ignoreCase = true) || it.name.contains("ler", ignoreCase = true) }
                        val academiaTask = dashboard.todayTasks.find { it.title.contains("academia", ignoreCase = true) }

                        val isAguaDone = aguaHabit?.isCompletedToday ?: false
                        val isAcademiaDone = academiaTask?.isCompleted ?: true
                        val isMeditacaoDone = meditacaoHabit?.isCompletedToday ?: false
                        val isLeituraDone = leituraHabit?.isCompletedToday ?: false

                        item {
                            val currentDayOfWeek = SimpleDateFormat("EEEE", Locale("pt", "BR")).format(Date()).replaceFirstChar { it.uppercase() }
                            val currentDayAndMonth = SimpleDateFormat("d 'de' MMMM", Locale("pt", "BR")).format(Date())
                            val currentDateFormatted = "$currentDayOfWeek, $currentDayAndMonth"

                            RetroTerminalBoard(
                                userName = if (userName == "Usuário" || userName == "Rafael") "Rafael" else userName,
                                currentDateStr = currentDateFormatted,
                                streakDays = dashboard.streak,
                                level = level,
                                totalXP = totalXP,
                                isAguaDone = isAguaDone,
                                isAcademiaDone = isAcademiaDone,
                                isMeditacaoDone = isMeditacaoDone,
                                isLeituraDone = isLeituraDone,
                                onToggleAgua = {
                                    aguaHabit?.let {
                                        viewModel.onEvent(DashboardEvent.CompleteHabit(it.id))
                                    } ?: run {
                                        viewModel.lifeOSViewModel.logWater(250)
                                    }
                                },
                                onToggleAcademia = {
                                    academiaTask?.let {
                                        viewModel.onEvent(DashboardEvent.OpenTask(it.id))
                                    } ?: run {
                                        viewModel.lifeOSViewModel.showToast("Tarefa Academia não encontrada.")
                                    }
                                },
                                onToggleMeditacao = {
                                    meditacaoHabit?.let {
                                        viewModel.onEvent(DashboardEvent.CompleteHabit(it.id))
                                    } ?: run {
                                        viewModel.lifeOSViewModel.showToast("Hábito Meditação não encontrado.")
                                    }
                                },
                                onToggleLeitura = {
                                    leituraHabit?.let {
                                        viewModel.onEvent(DashboardEvent.CompleteHabit(it.id))
                                    } ?: run {
                                        viewModel.lifeOSViewModel.showToast("Hábito Leitura não encontrado.")
                                    }
                                },
                                habitsProgress = if (dashboard.todayHabits.isNotEmpty()) (dashboard.todayHabits.count { it.isCompletedToday }.toFloat() / dashboard.todayHabits.size * 100).toInt() else 82,
                                tasksProgress = if (dashboard.todayTasks.isNotEmpty()) (dashboard.todayTasks.count { it.isCompleted }.toFloat() / dashboard.todayTasks.size * 100).toInt() else 71,
                                projectProgress = (54 + (dashboard.todayTasks.count { it.isCompleted } * 3) + (dashboard.todayHabits.count { it.isCompletedToday } * 2)).coerceIn(0, 100),
                                aiRecommendation = aiRecommendation,
                                completedGoals = ((dashboard.todayTasks.count { it.isCompleted } + dashboard.todayHabits.count { it.isCompletedToday })).coerceAtMost(5).coerceAtLeast(3),
                                totalGoals = 5,
                                onAIActionClick = { viewModel.lifeOSViewModel.generateAITasksSuggestions() },
                                onQuickActionClick = { module -> viewModel.lifeOSViewModel.setModule(module) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }

                        // ──────────── Separator before other widgets
                        item {
                            RetroDivider(isDouble = true)
                        }

                        item {
                            val pendingHabitsCount = dashboard.todayHabits.count { !it.isCompletedToday }.let { if (it == 0) 2 else it }
                            val priorityTasksCount = dashboard.todayTasks.count { !it.isCompleted && it.priority == com.example.core.common.TaskPriority.HIGH }.let { if (it == 0) 1 else it }
                            val streakCount = if (dashboard.streak == 0) 12 else dashboard.streak
                            AIInsightCard(
                                pendingHabitsCount = pendingHabitsCount,
                                priorityTasksCount = priorityTasksCount,
                                streakCount = streakCount,
                                onVerPlanoDoDiaClick = {
                                    viewModel.lifeOSViewModel.setModuleWithAiTab(com.example.core.common.NavigationModule.LIFE_AI, 1) // 1 is Planner tab
                                },
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        dashboard.dailyPlan?.let { plan ->
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .border(
                                            width = 1.dp,
                                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                                colors = listOf(
                                                    MaterialTheme.colorScheme.secondary,
                                                    MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                                                )
                                            ),
                                            shape = RoundedCornerShape(20.dp)
                                        ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Plano Inteligente",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.secondary,
                                                    letterSpacing = 0.5.sp
                                                )
                                            )
                                            
                                            Box(
                                                modifier = Modifier
                                                    .background(
                                                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = "Foco: ${plan.estimatedProductivity}%",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        plan.blocks.forEach { block ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Text(
                                                        text = block.start.toString(),
                                                        style = MaterialTheme.typography.labelMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                                        ),
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(16.dp))

                                                Column {
                                                    Text(
                                                        text = block.title,
                                                        style = MaterialTheme.typography.bodyLarge.copy(
                                                            fontWeight = FontWeight.Bold
                                                        ),
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (block.description.isNotBlank()) {
                                                        Text(
                                                            text = block.description,
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        if (plan.recommendations.isNotEmpty()) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 16.dp),
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                            )

                                            Text(
                                                text = "Recomendações do Copiloto",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.tertiary
                                                ),
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )

                                            plan.recommendations.forEach { recommendation ->
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 6.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = Icons.Default.Star,
                                                            contentDescription = "Recomendação",
                                                            tint = MaterialTheme.colorScheme.tertiary,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = recommendation.title,
                                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                                fontWeight = FontWeight.Bold
                                                            ),
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                    }
                                                    Text(
                                                        text = recommendation.description,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.padding(start = 24.dp, top = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Expandable detailed modules selector
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showDetails = !showDetails }
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (showDetails) "▲ Ocultar Módulos Detalhados" else "▼ Mostrar Módulos Detalhados",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.testTag("toggle_detailed_modules")
                                )
                            }
                        }

                        // Configured user dashboard widgets
                        if (showDetails) {
                            widgets.sortedBy { it.order }.forEach { widget ->
                            if (widget.isVisible) {
                                when (widget.id) {
                                    "quick_stats" -> {
                                        item(key = "quick_stats") {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                XPCard(
                                                    level = level,
                                                    totalXP = totalXP,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                StreakCard(
                                                    streakDays = dashboard.streak,
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }
                                    "tasks" -> {
                                        item(key = "tasks") {
                                            val aiTaskSuggestion by viewModel.lifeOSViewModel.aiTaskSuggestion.collectAsState()
                                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                                TaskCard(
                                                    tasks = dashboard.todayTasks,
                                                    aiSuggestion = aiTaskSuggestion,
                                                    onToggle = { viewModel.onEvent(DashboardEvent.OpenTask(it)) },
                                                    onAddTask = { title, priority -> viewModel.lifeOSViewModel.addTask(title, "Dashboard", priority) },
                                                    onGenerateAiSuggestion = { viewModel.lifeOSViewModel.generateAITasksSuggestions() }
                                                )
                                                ProjectCard(
                                                    projects = dashboard.projects,
                                                    projectProgress = projectProgress
                                                )
                                            }
                                        }
                                    }
                                    "habits" -> {
                                        item(key = "habits") {
                                            val aiHabitRecommendation by viewModel.lifeOSViewModel.aiHabitRecommendation.collectAsState()
                                            HabitCard(
                                                habits = dashboard.todayHabits,
                                                aiHabitRec = aiHabitRecommendation,
                                                onToggle = { viewModel.onEvent(DashboardEvent.CompleteHabit(it)) },
                                                onGenerateAiHabitRecommendation = { viewModel.lifeOSViewModel.generateAIHabitsRecommendation() }
                                            )
                                        }
                                    }
                                    "focus" -> {
                                        item(key = "focus") {
                                            FocusCard()
                                        }
                                    }
                                    "copilot_planner" -> {
                                        item(key = "copilot_planner") {
                                            LifeCopilotQuickWidget(
                                                onViewFullPlan = onNavigateToCopilot
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            ExecutiveSummaryWidget()
                                            Spacer(modifier = Modifier.height(16.dp))
                                            LifeGraphEngineWidget()
                                            Spacer(modifier = Modifier.height(16.dp))
                                            LifeCopilotGoalPlannerWidget(
                                                onPlanExecuted = { viewModel.onEvent(DashboardEvent.Refresh) }
                                            )
                                        }
                                    }
                                    "agenda" -> {
                                        item(key = "agenda") {
                                            CalendarCard(events = dashboard.events)
                                        }
                                    }
                                    "health" -> {
                                        item(key = "health") {
                                            HealthWidget(
                                                waterDrankMl = waterDrankMl,
                                                sleepHours = sleepHours,
                                                onLogWater = { viewModel.lifeOSViewModel.logWater(it) },
                                                onResetWater = { viewModel.lifeOSViewModel.resetWater() },
                                                onUpdateSleep = { viewModel.lifeOSViewModel.logSleepHours(it) }
                                            )
                                        }
                                    }
                                    "project_workflow" -> {
                                        item(key = "project_workflow") {
                                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                                LifeAIPipelineWidget()
                                                ProjectWorkflowWidget(
                                                    onStartFocusSession = { /* Start Focus Session */ }
                                                )
                                            }
                                        }
                                    }
                                    "smart_timeline" -> {
                                        item(key = "smart_timeline") {
                                            SmartTimelineWidget()
                                        }
                                    }
                                    "focus_analytics" -> {
                                        item(key = "focus_analytics") {
                                            FocusAnalyticsWidget()
                                        }
                                    }
                                    "financial_widget" -> {
                                        item(key = "financial_widget") {
                                            FinancialWidget(
                                                onGenerateAiAdvice = { viewModel.lifeOSViewModel.generateAIFinancialAdvice() }
                                            )
                                        }
                                    }
                                    "health_widget" -> {
                                        item(key = "health_widget") {
                                            LifeOSHealthWidget(
                                                initialWaterMl = waterDrankMl,
                                                sleepHours = sleepHours.toDouble(),
                                                onLogWater = { viewModel.lifeOSViewModel.logWater(it) }
                                            )
                                        }
                                    }
                                    "knowledge" -> {
                                        item(key = "knowledge") {
                                            KnowledgeWidget(
                                                recentNotesCount = 12,
                                                unreviewedIdeasCount = 3,
                                                favoriteDocsCount = 2,
                                                onOpenKnowledgeBase = { /* Open Knowledge Base */ }
                                            )
                                        }
                                    }
                                    "chart" -> {
                                        item(key = "chart") {
                                            StatsCard(rates = dashboard.weeklyStats.rates)
                                        }
                                    }
                                    "finances" -> {
                                        item(key = "finances") {
                                            val aiFinanceAdvice by viewModel.lifeOSViewModel.aiFinanceAdvice.collectAsState()
                                            FinanceCard(
                                                transactions = transactions,
                                                aiFinanceMsg = aiFinanceAdvice,
                                                onGenerateAiFinanceAdvice = { viewModel.lifeOSViewModel.generateAIFinancialAdvice() }
                                            )
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
    }
}

// Stats helper structure
data class NavigationStatItem(
    val title: String,
    val value: String,
    val targetModule: NavigationModule
)

@Composable
fun ChecklistItemRow(
    text: String,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isCompleted) "✓" else "○",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = if (isCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                fontWeight = FontWeight.Medium
            ),
            color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun RetroTerminalBoard(
    userName: String,
    currentDateStr: String,
    streakDays: Int,
    level: Int,
    totalXP: Int,
    isAguaDone: Boolean,
    isAcademiaDone: Boolean,
    isMeditacaoDone: Boolean,
    isLeituraDone: Boolean,
    onToggleAgua: () -> Unit,
    onToggleAcademia: () -> Unit,
    onToggleMeditacao: () -> Unit,
    onToggleLeitura: () -> Unit,
    habitsProgress: Int,
    tasksProgress: Int,
    projectProgress: Int,
    aiRecommendation: String,
    completedGoals: Int,
    totalGoals: Int,
    onAIActionClick: () -> Unit,
    onQuickActionClick: (NavigationModule) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val termColor = MaterialTheme.colorScheme.primary
    val termOnSurface = MaterialTheme.colorScheme.onSurface
    val termOnVariant = MaterialTheme.colorScheme.onSurfaceVariant
    
    var isMissionStarted by remember { mutableStateOf(false) }
    var isNightMode by remember { mutableStateOf(false) }

    val displayStreak = if (streakDays > 0) streakDays else 28
    val displayLevel = if (level > 0) level else 15
    val displayHabitsProgress = if (habitsProgress > 0) habitsProgress else 82
    val displayProjectProgress = if (projectProgress > 0) projectProgress else 78

    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 380.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1115) // Sleek Obsidian deep black
        ),
        border = BorderStroke(
            width = 1.dp,
            color = termColor.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Period Selector Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STATUS:",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = termOnVariant.copy(alpha = 0.5f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (!isNightMode) "[☀ Dia]" else " ☀ Dia ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isNightMode) termColor else termOnVariant,
                        modifier = Modifier.clickable { isNightMode = false }
                    )
                    Text(
                        text = if (isNightMode) "[🌙 Noite]" else " 🌙 Noite ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isNightMode) termColor else termOnVariant,
                        modifier = Modifier.clickable { isNightMode = true }
                    )
                }
            }

            if (!isNightMode) {
                // Header Top Separator
                Text(
                    text = "┌────────────────────────────────────┐",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Greeting Row
                Text(
                    text = "👋 Bom dia, $userName",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termColor,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Life Score Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Life Score",
                        fontFamily = FontFamily.Monospace,
                        color = termOnVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "92",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = termOnSurface,
                        fontSize = 13.sp
                    )
                }

                // Momentum Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Momentum",
                        fontFamily = FontFamily.Monospace,
                        color = termOnVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "🔥 24 dias",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5722),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Separator 1
                Text(
                    text = "─────────────────────────────────────",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Mission Title
                Text(
                    text = "🎯 Missão",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termColor,
                    fontSize = 14.sp
                )

                // Task Name
                Text(
                    text = "Implementar Dashboard Premium",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = termOnSurface,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Interactive Começar Agora Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (isMissionStarted) termColor.copy(alpha = 0.1f)
                            else termColor.copy(alpha = 0.15f)
                        )
                        .border(1.dp, termColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .clickable { isMissionStarted = !isMissionStarted }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isMissionStarted) "[ Em Progresso ]" else "[ Começar Agora ]",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = termColor,
                        fontSize = 12.sp
                    )
                }

                // Separator 2
                Text(
                    text = "─────────────────────────────────────",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Progresso Section
                Text(
                    text = "📈 Progresso",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termColor,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "• Objetivos: 80%\n• Projetos: 65%\n• Hábitos: 88%",
                    fontFamily = FontFamily.Monospace,
                    color = termOnVariant,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                // Separator 3
                Text(
                    text = "─────────────────────────────────────",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // LifeAI Header
                Text(
                    text = "🤖 LifeAI",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termColor,
                    fontSize = 14.sp
                )

                Text(
                    text = "Sugestões:\n\"Se concluir o Dashboard Premium hoje,\nsua pontuação do Life Score aumentará para 95.\"",
                    fontFamily = FontFamily.Monospace,
                    color = termOnVariant,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                // Separator 4
                Text(
                    text = "─────────────────────────────────────",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Agenda Section
                Text(
                    text = "📅 Agenda",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termColor,
                    fontSize = 14.sp
                )

                Text(
                    text = "• 09:30 - Reunião de Alinhamento\n• 11:00 - Sprint de Foco Dashboard\n• 15:00 - Revisão de Métricas",
                    fontFamily = FontFamily.Monospace,
                    color = termOnSurface,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                // Separator 5
                Text(
                    text = "─────────────────────────────────────",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Quick Actions Header
                Text(
                    text = "⚡ Ações rápidas",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termColor,
                    fontSize = 14.sp
                )

                val quickActionItems = listOf(
                    "Nova tarefa" to NavigationModule.TASKS,
                    "Novo hábito" to NavigationModule.HABITS,
                    "Novo projeto" to NavigationModule.PROJECTS,
                    "Iniciar foco" to NavigationModule.TASKS,
                    "Perguntar ao LifeAI" to NavigationModule.LIFE_AI
                )

                quickActionItems.forEach { (label, module) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onQuickActionClick(module) }
                            .padding(vertical = 4.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "○ ",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = termColor,
                            fontSize = 14.sp
                        )
                        Text(
                            text = label,
                            fontFamily = FontFamily.Monospace,
                            color = termOnSurface,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                // Header Top Separator
                Text(
                    text = "┌────────────────────────────────────┐",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Evening Greeting Row
                Text(
                    text = "Boa noite.",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termColor,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Hoje você:",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termOnSurface,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "✓ concluiu 7 tarefas\n\n✓ fez 4 hábitos\n\n✓ trabalhou 2h18 focado",
                    fontFamily = FontFamily.Monospace,
                    color = termOnVariant,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )

                // Separator 1
                Text(
                    text = "─────────────────────────────────────",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Life Score Delta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Life Score",
                        fontFamily = FontFamily.Monospace,
                        color = termOnVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "+4",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Momentum Delta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Momentum",
                        fontFamily = FontFamily.Monospace,
                        color = termOnVariant,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "+1",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5722),
                        fontSize = 13.sp
                    )
                }

                // Separator 2
                Text(
                    text = "─────────────────────────────────────",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Closing Row
                Text(
                    text = "Excelente dia.",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = termColor,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                // Bottom Separator
                Text(
                    text = "─────────────────────────────────────",
                    fontFamily = FontFamily.Monospace,
                    color = termColor.copy(alpha = 0.4f),
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun getTextProgressBar(progress: Float, length: Int = 10): String {
    val filledCount = (progress.coerceIn(0f, 1f) * length).toInt()
    val unfilledCount = (length - filledCount).coerceAtLeast(0)
    return "█".repeat(filledCount) + "░".repeat(unfilledCount)
}

package com.example.core.common

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.*
import com.example.features.dashboard.domain.model.DashboardData
import com.example.features.dashboard.domain.model.WeeklyStats
import com.example.features.dashboard.domain.model.FinanceSummary
import com.example.features.dashboard.domain.model.AIInsight
import com.example.core.database.AppDatabase
import com.lifeos.features.tasks.domain.repository.TaskRepository
import com.example.features.projects.domain.repository.ProjectRepository
import com.example.features.projects.data.repository.ProjectRepositoryImpl
import com.example.core.network.GeminiServiceClient
import com.example.core.network.SupabaseServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Global Toast Types for premium UI feedback
enum class ToastType {
    SUCCESS, ERROR, INFO, WARNING
}

data class GlobalToast(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val type: ToastType = ToastType.INFO
)

// Configurable Dashboard Widget descriptor
data class DashboardWidget(
    val id: String,
    val title: String,
    val isVisible: Boolean = true,
    val order: Int
)

class LifeOSViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskRepository: TaskRepository = com.lifeos.features.tasks.data.repository.TaskRepositoryImpl(database.taskDao(), application)
    private val projectRepository: ProjectRepository = ProjectRepositoryImpl(database.projectDao(), application)

    private val sharedPrefs by lazy {
        application.getSharedPreferences("lifeos_prefs", Context.MODE_PRIVATE)
    }

    // Auth State
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Persistent App Theme (default to dark)
    private val _isDarkMode = MutableStateFlow(sharedPrefs.getBoolean("dark_mode", true))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Current Active Navigation Module
    private val _currentModule = MutableStateFlow(NavigationModule.DASHBOARD)
    val currentModule: StateFlow<NavigationModule> = _currentModule.asStateFlow()

    // Initial AI Tab (0: Chat, 1: Planner, 2: Recommendations, 3: Memories)
    private val _initialAiTab = MutableStateFlow(0)
    val initialAiTab: StateFlow<Int> = _initialAiTab.asStateFlow()

    // Global Interactive Loading Overlay
    private val _isGlobalLoading = MutableStateFlow(false)
    val isGlobalLoading: StateFlow<Boolean> = _isGlobalLoading.asStateFlow()

    // Global Error Message Banner
    private val _globalError = MutableStateFlow<String?>(null)
    val globalError: StateFlow<String?> = _globalError.asStateFlow()

    // Global Structured Toast Messages
    private val _toast = MutableStateFlow<GlobalToast?>(null)
    val toast: StateFlow<GlobalToast?> = _toast.asStateFlow()

    // App Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- Configurable Dashboard Widgets State ---
    private val _widgets = MutableStateFlow<List<DashboardWidget>>(listOf(
        DashboardWidget("executive_summary", "📊 Resumo Executivo & IA", isVisible = true, order = 0),
        DashboardWidget("copilot_planner", "Life Copilot • Planejador Meta 90 Dias", isVisible = true, order = 1),
        DashboardWidget("agenda", "Minha Agenda", isVisible = true, order = 2),
        DashboardWidget("project_workflow", "Pipeline do Projeto", isVisible = true, order = 2),
        DashboardWidget("smart_timeline", "Timeline Inteligente", isVisible = true, order = 3),
        DashboardWidget("tasks", "Tarefas do Dia", isVisible = true, order = 4),
        DashboardWidget("quick_stats", "Métricas Rápidas", isVisible = true, order = 5),
        DashboardWidget("focus_analytics", "Focus Analytics", isVisible = true, order = 6),
        DashboardWidget("financial_widget", "Widget Financeiro", isVisible = true, order = 7),
        DashboardWidget("health_widget", "Widget Saúde", isVisible = true, order = 8),
        DashboardWidget("knowledge", "📚 Conhecimento", isVisible = true, order = 9),
        DashboardWidget("habits", "Hábitos Saudáveis", isVisible = true, order = 10),
        DashboardWidget("chart", "Gráfico de Produtividade", isVisible = true, order = 11)
    ))
    val widgets: StateFlow<List<DashboardWidget>> = _widgets.asStateFlow()

    // --- User Dashboard Metrics State ---
    private val _userMetrics = MutableStateFlow<UserDashboardMetrics>(
        UserDashboardMetrics(
            userId = "mock-uuid-1234",
            tasksToday = 8,
            completedToday = 0,
            habitStreak = 18,
            weeklyGoal = 82,
            pendingProjects = 4,
            lastAiSummary = "Bom dia Rafael!\n\nHoje você possui:\n✔ 8 tarefas\n✔ 2 reuniões\n✔ Academia às 18h\n✔ Meta semanal em 82%\n\nMinha recomendação:\nFinalize primeiro as tarefas prioritárias e deixe as de baixa prioridade para depois do almoço.",
            updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )
    )
    val userMetrics: StateFlow<UserDashboardMetrics> = _userMetrics.asStateFlow()

    // --- Mock / Live Data states (kept for existing feature stability) ---

    // Tasks
    private val _tasks = MutableStateFlow<List<TaskItem>>(listOf(
        TaskItem(title = "Comprar combustível", category = "Pessoal", priority = TaskPriority.MEDIUM, dueDate = "Hoje", status = com.example.core.common.TaskStatus.TO_DO, tags = listOf("Casa"), isFavorite = true),
        TaskItem(title = "Estudar Kotlin", category = "Estudos", priority = TaskPriority.HIGH, dueDate = "Amanhã", status = com.example.core.common.TaskStatus.TO_DO, tags = listOf("Android", "IA"), isFavorite = true),
        TaskItem(title = "Academia", category = "Saúde", priority = TaskPriority.MEDIUM, dueDate = "Hoje", isCompleted = true, status = com.example.core.common.TaskStatus.COMPLETED, tags = listOf("Casa")),
        TaskItem(title = "Revisar relatório trimestral", category = "Trabalho", priority = TaskPriority.HIGH, dueDate = "Semana", status = com.example.core.common.TaskStatus.IN_PROGRESS, tags = listOf("Urgente", "Android")),
        TaskItem(title = "Configurar banco de dados Supabase", category = "Tecnologia", priority = TaskPriority.HIGH, dueDate = "Mês", status = com.example.core.common.TaskStatus.TO_DO, tags = listOf("Urgente", "Supabase")),
        TaskItem(title = "Enviar declaração anual atrasada", category = "Finanças", priority = TaskPriority.HIGH, dueDate = "Atrasadas", status = com.example.core.common.TaskStatus.TO_DO, tags = listOf("Urgente"))
    ))
    val tasks: StateFlow<List<TaskItem>> = _tasks.asStateFlow()

    // Projects
    private val _projects = MutableStateFlow<List<Project>>(listOf(
        Project(id = "p1", name = "🚀 LifeOS AI Framework", color = "#7E57C2"),
        Project(id = "p2", name = "📚 Mestrado Acadêmico", color = "#29B6F6"),
        Project(id = "p3", name = "🏡 Reforma da Casa", color = "#FFA726")
    ))
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    // Agenda Events
    private val _agendaEvents = MutableStateFlow<List<AgendaEvent>>(listOf(
        AgendaEvent(title = "Academia", time = "08:00", category = "Saúde", description = "Treino matinal", isCompleted = true),
        AgendaEvent(title = "Reunião", time = "09:30", category = "Trabalho", description = "Alinhamento de projeto"),
        AgendaEvent(title = "Landing Page", time = "10:30", category = "Trabalho", description = "Design e desenvolvimento", impact = "★★★★★"),
        AgendaEvent(title = "Almoço", time = "13:00", category = "Pessoal", description = "Pausa para refeição"),
        AgendaEvent(title = "Projeto", time = "15:00", category = "Desenvolvimento", description = "Sessão de foco no projeto"),
        AgendaEvent(title = "Leitura", time = "17:30", category = "Desenvolvimento", description = "30 minutos de leitura")
    ))
    val agendaEvents: StateFlow<List<AgendaEvent>> = _agendaEvents.asStateFlow()

    // Habits
    private val firestoreHabitRepo = com.lifeos.features.habits.data.repository.FirestoreHabitRepository()

    private val _habits = MutableStateFlow<List<HabitItem>>(listOf(
        HabitItem(name = "Beber 2.5L de água", streak = 5, isCompletedToday = false, icon = "💧", category = "Saúde", targetDaysPerWeek = 7, completedThisWeek = 5),
        HabitItem(name = "Meditação Matinal", streak = 12, isCompletedToday = true, icon = "🧘", category = "Mente", targetDaysPerWeek = 7, completedThisWeek = 6),
        HabitItem(name = "Leitura (15 páginas)", streak = 3, isCompletedToday = false, icon = "📚", category = "Mente", targetDaysPerWeek = 5, completedThisWeek = 3),
        HabitItem(name = "Dormir 8 Horas", streak = 7, isCompletedToday = true, icon = "🌙", category = "Saúde", targetDaysPerWeek = 7, completedThisWeek = 7)
    ))
    val habits: StateFlow<List<HabitItem>> = _habits.asStateFlow()

    // Goals (Metas a Longo Prazo)
    private val _goals = MutableStateFlow<List<GoalItem>>(listOf(
        GoalItem(title = "🏃 Maratona de Berlim (42K)", targetValue = "42 km", progressValue = "25 km", progressPercent = 0.59f),
        GoalItem(title = "📚 Ler 24 livros no ano", targetValue = "24 livros", progressValue = "12 livros", progressPercent = 0.50f),
        GoalItem(title = "💰 Reserva de Emergência", targetValue = "R$ 30.000", progressValue = "R$ 15.000", progressPercent = 0.50f)
    ))
    val goals: StateFlow<List<GoalItem>> = _goals.asStateFlow()

    fun addGoal(title: String, targetValue: String, progressValue: String, progressPercent: Float) {
        if (title.isBlank()) return
        val newGoal = GoalItem(
            title = title,
            targetValue = targetValue,
            progressValue = progressValue,
            progressPercent = progressPercent
        )
        _goals.value = _goals.value + newGoal
        showToast("Meta criada: '$title'", ToastType.SUCCESS)
    }

    fun updateGoalProgress(id: String, progressValue: String, progressPercent: Float) {
        _goals.value = _goals.value.map {
            if (it.id == id) it.copy(
                progressValue = progressValue,
                progressPercent = progressPercent,
                isCompleted = progressPercent >= 1.0f
            )
            else it
        }
        showToast("Progresso da meta atualizado!", ToastType.SUCCESS)
    }

    fun scheduleOptimalTask(taskTitle: String, timeSlot: String, category: String = "Foco IA") {
        addEvent(
            title = taskTitle,
            time = timeSlot,
            category = category,
            description = "Sessão de foco recomendada pela IA para $taskTitle"
        )
    }

    // Finances (Transactions)
    private val _transactions = MutableStateFlow<List<Transaction>>(listOf(
        Transaction(title = "Salário mensal SaaS", amount = 8500.00, isIncome = true, category = "Salário", date = "14 Jul"),
        Transaction(title = "Café & Pão de Queijo", amount = 15.50, isIncome = false, category = "Alimentação", date = "14 Jul"),
        Transaction(title = "Uber para escritório", amount = 28.90, isIncome = false, category = "Transporte", date = "13 Jul"),
        Transaction(title = "Supermercado CompreBem", amount = 350.20, isIncome = false, category = "Alimentação", date = "10 Jul"),
        Transaction(title = "Venda freelancer App", amount = 2400.00, isIncome = true, category = "Freelance", date = "01 Jul"),
        Transaction(title = "Assinatura Notion Pro", amount = 48.00, isIncome = false, category = "Assinaturas", date = "29 Jun")
    ))
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    // Health: Water & Sleep
    private val _waterDrankMl = MutableStateFlow(1200) // target is 2500ml
    val waterDrankMl: StateFlow<Int> = _waterDrankMl.asStateFlow()

    private val _sleepHours = MutableStateFlow(7.5f) // target is 8h
    val sleepHours: StateFlow<Float> = _sleepHours.asStateFlow()

    // LifeAI Chat Logs
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("init", "Olá! Sou o assistente LifeOS AI. Estou aqui para planejar seu dia, sugerir hábitos e responder qualquer dúvida de produtividade. Como posso te apoiar hoje?", false)
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // AI suggestion helpers to store modular prompt replies
    private val _aiTaskSuggestion = MutableStateFlow<String?>(null)
    val aiTaskSuggestion: StateFlow<String?> = _aiTaskSuggestion.asStateFlow()

    private val _aiFinanceAdvice = MutableStateFlow<String?>(null)
    val aiFinanceAdvice: StateFlow<String?> = _aiFinanceAdvice.asStateFlow()

    private val _aiHabitRecommendation = MutableStateFlow<String?>(null)
    val aiHabitRecommendation: StateFlow<String?> = _aiHabitRecommendation.asStateFlow()

    // Temporary toast support for deprecated strings.xml callbacks
    val toastMessage = MutableStateFlow<String?>(null)

    private val dashboardCombined1 = combine(
        userMetrics,
        tasks,
        habits,
        agendaEvents
    ) { metrics, tasksList, habitsList, agendaList ->
        listOf(metrics, tasksList, habitsList, agendaList)
    }

    private val dashboardCombined2 = combine(
        projects,
        aiTaskSuggestion,
        aiHabitRecommendation,
        transactions
    ) { projectsList, taskSuggest, habitRec, txList ->
        listOf(projectsList, taskSuggest, habitRec, txList)
    }

    val dashboard: StateFlow<DashboardData> = combine(dashboardCombined1, dashboardCombined2) { list1, list2 ->
        val metrics = list1[0] as UserDashboardMetrics
        val tasksList = list1[1] as List<TaskItem>
        val habitsList = list1[2] as List<HabitItem>
        val agendaList = list1[3] as List<AgendaEvent>
        val projectsList = list2[0] as List<Project>
        val taskSuggest = list2[1] as? String
        val habitRec = list2[2] as? String
        @Suppress("UNCHECKED_CAST")
        val txList = list2[3] as List<Transaction>

        val suggestions = mutableListOf<String>()
        taskSuggest?.let { suggestions.add(it) }
        habitRec?.let { suggestions.add(it) }
        if (suggestions.isEmpty()) {
            suggestions.add("Mantenha o foco em suas metas diárias!")
            suggestions.add("Beba água para manter a saúde e energia em alta.")
        }
        
        val completedTasks = tasksList.count { it.isCompleted }
        val totalTasks = tasksList.size
        val taskCompletionRate = DashboardMetricsCalculator.calculateTaskCompletionRate(completedTasks, totalTasks)

        val completedHabits = habitsList.count { it.isCompletedToday }
        val totalHabits = habitsList.size
        val habitCompletionRate = DashboardMetricsCalculator.calculateHabitCompletionRate(completedHabits, totalHabits)

        val averageRate = DashboardMetricsCalculator.calculateAverageRate(taskCompletionRate, habitCompletionRate)

        val streak = metrics.habitStreak
        val totalXP = DashboardMetricsCalculator.calculateXP(
            completedTasksCount = completedTasks,
            completedHabitsCount = completedHabits,
            streakDays = streak
        )
        val level = DashboardMetricsCalculator.calculateLevel(totalXP)
        
        val auth = _authState.value
        val name = if (auth is AuthState.Authenticated) auth.name else "Rafael"
        val email = if (auth is AuthState.Authenticated) auth.email else "rafaeltj16@gmail.com"
        val userId = metrics.userId
        
        val user = User(
            id = userId,
            email = email,
            name = name,
            level = level,
            xp = totalXP,
            streak = streak
        )

        val weeklyStats = DashboardMetricsCalculator.calculateWeeklyStats(
            completedTasks = completedTasks,
            completedHabits = completedHabits,
            totalTasks = totalTasks,
            totalHabits = totalHabits,
            averageRate = averageRate
        )

        val financeSummary = DashboardMetricsCalculator.calculateFinanceSummary(txList)

        val aiInsight = AIInsight(
            summary = metrics.lastAiSummary,
            recommendations = suggestions
        )

        DashboardData(
            user = user,
            xp = totalXP,
            level = level,
            streak = streak,
            todayHabits = habitsList,
            todayTasks = tasksList,
            projects = projectsList,
            events = agendaList,
            weeklyStats = weeklyStats,
            financeSummary = financeSummary,
            aiInsight = aiInsight
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardData()
    )

    init {
        // Collect from local Room database reactively to keep UI in sync
        viewModelScope.launch {
            taskRepository.getTasksFlow().collect { localTasks ->
                if (localTasks.isNotEmpty() || _authState.value is AuthState.Authenticated) {
                    _tasks.value = localTasks
                }
            }
        }

        viewModelScope.launch {
            projectRepository.getProjectsFlow().collect { localProjects ->
                if (localProjects.isNotEmpty() || _authState.value is AuthState.Authenticated) {
                    _projects.value = localProjects
                }
            }
        }

        // Recover persistent session if available
        val storedEmail = sharedPrefs.getString("user_email", null)
        val storedName = sharedPrefs.getString("user_name", null)
        if (storedEmail != null && storedName != null) {
            _authState.value = AuthState.Authenticated(storedEmail, storedName)
            loadUserMetrics(storedEmail)
        }

        // Start listening to Firestore Habits
        try {
            firestoreHabitRepo.observeHabits(
                onSuccess = { remoteHabits ->
                    if (remoteHabits.isNotEmpty()) {
                        _habits.value = remoteHabits
                    }
                },
                onError = { error ->
                    Log.e("LifeOSViewModel", "Firestore habits observe error", error)
                }
            )
        } catch (e: Exception) {
            Log.e("LifeOSViewModel", "Failed to start Firestore habits listener", e)
        }
    }

    fun loadUserMetrics(userId: String) {
        viewModelScope.launch {
            try {
                val metricsDto = SupabaseServiceClient.getDashboardMetrics(userId)
                val metrics = com.example.features.dashboard.data.mapper.DashboardMapper.toDomain(metricsDto)
                _userMetrics.value = metrics
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to load metrics", e)
            }
        }
        loadUserTasks(userId)
        loadUserProjects(userId)
    }

    private fun mapDueToTimestamp(dueOption: String): String {
        val calendar = Calendar.getInstance()
        when (dueOption) {
            "Hoje" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
            }
            "Amanhã" -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
            }
            "Semana" -> {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
            }
            "Mês" -> {
                calendar.add(Calendar.DAY_OF_YEAR, 30)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
            }
            "Atrasadas", "Atrasada" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
            }
            else -> {
                if (dueOption.contains("-") && dueOption.contains("T")) {
                    return dueOption
                }
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
            }
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(calendar.time)
    }

    private fun mapTimestampToDue(timestampStr: String?): String {
        if (timestampStr.isNullOrBlank()) return "Hoje"
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val cleanStr = timestampStr.substringBefore(".").substringBefore("+").substringBefore("Z")
            val taskDate = sdf.parse(cleanStr) ?: return "Hoje"
            
            val calendarTask = Calendar.getInstance().apply { time = taskDate }
            val calendarToday = Calendar.getInstance()
            
            val dTask = clearTime(calendarTask).timeInMillis
            val dToday = clearTime(calendarToday).timeInMillis
            
            val diffDays = ((dTask - dToday) / (24 * 60 * 60 * 1000)).toInt()
            
            return when {
                diffDays < 0 -> "Atrasadas"
                diffDays == 0 -> "Hoje"
                diffDays == 1 -> "Amanhã"
                diffDays in 2..7 -> "Semana"
                else -> "Mês"
            }
        } catch (e: Exception) {
            Log.e("LifeOSViewModel", "Error parsing timestamp: $timestampStr", e)
            val trimmed = timestampStr.trim()
            if (trimmed in listOf("Hoje", "Amanhã", "Semana", "Mês", "Atrasadas", "Atrasada")) {
                return if (trimmed == "Atrasada") "Atrasadas" else trimmed
            }
            return "Hoje"
        }
    }

    private fun clearTime(calendar: Calendar): Calendar {
        return (calendar.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    fun loadUserTasks(email: String) {
        viewModelScope.launch {
            try {
                val userUuid = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
                // Sync remote tasks into Room cache database
                taskRepository.syncWithRemote(userUuid)
                
                // Retrieve locally cached tasks from Room database
                val localTasks = taskRepository.getTasks(userUuid)
                _tasks.value = localTasks
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to load/sync tasks in Repository", e)
            }
        }
    }

    fun loadUserProjects(email: String) {
        viewModelScope.launch {
            try {
                val userUuid = UUID.nameUUIDFromBytes(email.toByteArray()).toString()
                // Sync remote projects into Room cache database
                projectRepository.syncWithRemote(userUuid)
                
                // Retrieve locally cached projects from Room database
                val localProjects = projectRepository.getProjects(userUuid)
                _projects.value = localProjects
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to load/sync projects in Repository", e)
            }
        }
    }

    private fun getCurrentUserId(): String {
        return when (val auth = _authState.value) {
            is AuthState.Authenticated -> UUID.nameUUIDFromBytes(auth.email.toByteArray()).toString()
            else -> "mock-uuid-1234"
        }
    }

    fun addSubTask(taskId: String, title: String) {
        val newSubTask = SubTask(
            id = UUID.randomUUID().toString(),
            taskId = taskId,
            title = title,
            completed = false
        )
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(subTasks = task.subTasks + newSubTask)
            } else task
        }

        viewModelScope.launch {
            try {
                taskRepository.saveSubTask(newSubTask)
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to save subtask in Repository", e)
            }
        }
    }

    fun toggleSubTask(taskId: String, subTaskId: String) {
        var updatedSubTask: SubTask? = null
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                val newSubTasks = task.subTasks.map { sub ->
                    if (sub.id == subTaskId) {
                        val updated = sub.copy(completed = !sub.completed)
                        updatedSubTask = updated
                        updated
                    } else sub
                }
                task.copy(subTasks = newSubTasks)
            } else task
        }

        val sub = updatedSubTask
        if (sub != null) {
            viewModelScope.launch {
                try {
                    taskRepository.saveSubTask(sub)
                } catch (e: Exception) {
                    Log.e("LifeOSViewModel", "Failed to toggle subtask in Repository", e)
                }
            }
        }
    }

    fun deleteSubTask(taskId: String, subTaskId: String) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(subTasks = task.subTasks.filter { it.id != subTaskId })
            } else task
        }

        viewModelScope.launch {
            try {
                taskRepository.deleteSubTask(taskId, subTaskId)
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to delete subtask from Repository", e)
            }
        }
    }

    private fun syncTaskToSupabase(taskItem: TaskItem, isDeletion: Boolean = false) {
        val userUuid = getCurrentUserId()
        viewModelScope.launch {
            try {
                if (isDeletion) {
                    taskRepository.deleteTask(userUuid, taskItem.id)
                } else {
                    taskRepository.saveTask(userUuid, taskItem)
                }
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to sync task: ${taskItem.id}", e)
            }
        }
    }

    fun updateAndSaveMetrics(update: (UserDashboardMetrics) -> UserDashboardMetrics) {
        viewModelScope.launch {
            try {
                val current = _userMetrics.value
                val updated = update(current).copy(
                    updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                )
                _userMetrics.value = updated
                val dto = com.example.features.dashboard.data.mapper.DashboardMapper.toDto(updated)
                SupabaseServiceClient.saveDashboardMetrics(dto)
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to save metrics", e)
            }
        }
    }

    // --- Actions ---

    fun showToast(msg: String, type: ToastType = ToastType.INFO) {
        _toast.value = GlobalToast(message = msg, type = type)
        toastMessage.value = msg // backwards compatibility
    }

    fun showToast(msg: String) {
        showToast(msg, ToastType.INFO)
    }

    fun clearToast() {
        _toast.value = null
        toastMessage.value = null
    }

    fun dismissError() {
        _globalError.value = null
    }

    fun toggleTheme() {
        val newTheme = !_isDarkMode.value
        _isDarkMode.value = newTheme
        sharedPrefs.edit().putBoolean("dark_mode", newTheme).apply()
        showToast("Tema alterado com sucesso!", ToastType.SUCCESS)
    }

    fun setModule(module: NavigationModule) {
        _currentModule.value = module
    }

    fun setModuleWithAiTab(module: NavigationModule, tabIndex: Int) {
        _initialAiTab.value = tabIndex
        _currentModule.value = module
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Supabase Authentication Real Integrations ---

    fun signIn(email: String, name: String) {
        viewModelScope.launch {
            _isGlobalLoading.value = true
            _globalError.value = null
            try {
                // Perform real Supabase REST auth / offline simulator callback
                val response = SupabaseServiceClient.authenticate(email, name.ifEmpty { "123456" })
                val user = response.user
                val finalName = user?.userMetadata?.get("name") ?: name.ifEmpty { "Usuário LifeOS" }
                val finalEmail = user?.email ?: email

                sharedPrefs.edit()
                    .putString("user_email", finalEmail)
                    .putString("user_name", finalName)
                    .apply()

                _authState.value = AuthState.Authenticated(finalEmail, finalName)
                showToast("Boas-vindas de volta, $finalName!", ToastType.SUCCESS)
                loadUserMetrics(finalEmail)
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Supabase authentication failed", e)
                _globalError.value = e.localizedMessage ?: "Falha ao autenticar com Supabase."
                showToast("Erro na autenticação", ToastType.ERROR)
            } finally {
                _isGlobalLoading.value = false
            }
        }
    }

    fun signUp(name: String, email: String) {
        viewModelScope.launch {
            _isGlobalLoading.value = true
            _globalError.value = null
            try {
                // Perform real Supabase sign up
                val response = SupabaseServiceClient.register(name, email, "123456")
                val user = response.user
                val finalName = user?.userMetadata?.get("name") ?: name
                val finalEmail = user?.email ?: email

                sharedPrefs.edit()
                    .putString("user_email", finalEmail)
                    .putString("user_name", finalName)
                    .apply()

                _authState.value = AuthState.Authenticated(finalEmail, finalName)
                showToast("Sua conta foi criada no Supabase!", ToastType.SUCCESS)
                loadUserMetrics(finalEmail)
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Supabase registration failed", e)
                _globalError.value = e.localizedMessage ?: "Falha ao registrar conta no Supabase."
                showToast("Erro no registro", ToastType.ERROR)
            } finally {
                _isGlobalLoading.value = false
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _isGlobalLoading.value = true
            _globalError.value = null
            try {
                SupabaseServiceClient.recoverPassword(email)
                showToast("E-mail de recuperação enviado via Supabase para $email", ToastType.SUCCESS)
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Supabase password recovery failed", e)
                _globalError.value = e.localizedMessage ?: "Erro ao enviar e-mail de recuperação."
                showToast("Falha na recuperação", ToastType.ERROR)
            } finally {
                _isGlobalLoading.value = false
            }
        }
    }

    fun signOut() {
        sharedPrefs.edit()
            .remove("user_email")
            .remove("user_name")
            .apply()
        _authState.value = AuthState.Unauthenticated
        _currentModule.value = NavigationModule.DASHBOARD
        showToast("Sessão encerrada com sucesso", ToastType.INFO)
    }

    // --- Drag & Drop Reorderable Configurable Dashboard Widgets Actions ---

    fun toggleWidgetVisibility(id: String) {
        _widgets.value = _widgets.value.map {
            if (it.id == id) it.copy(isVisible = !it.isVisible) else it
        }
        showToast("Configurações do widget atualizadas", ToastType.SUCCESS)
    }

    fun moveWidgetUp(id: String) {
        val list = _widgets.value.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index > 0) {
            val temp = list[index]
            list[index] = list[index - 1]
            list[index - 1] = temp

            // Reassign orders
            _widgets.value = list.mapIndexed { idx, widget -> widget.copy(order = idx) }
        }
    }

    fun moveWidgetDown(id: String) {
        val list = _widgets.value.toMutableList()
        val index = list.indexOfFirst { it.id == id }
        if (index != -1 && index < list.size - 1) {
            val temp = list[index]
            list[index] = list[index + 1]
            list[index + 1] = temp

            _widgets.value = list.mapIndexed { idx, widget -> widget.copy(order = idx) }
        }
    }

    // --- Task Actions ---
    fun addTask(title: String, category: String, priority: TaskPriority, status: com.example.core.common.TaskStatus = com.example.core.common.TaskStatus.TO_DO, tags: List<String> = emptyList(), dueDate: String = "Hoje", isFavorite: Boolean = false, projectId: String? = null) {
        if (title.isBlank()) return
        val newTask = TaskItem(
            title = title,
            category = if (category.isBlank()) "Geral" else category,
            priority = priority,
            dueDate = dueDate,
            status = status,
            isCompleted = (status == com.example.core.common.TaskStatus.COMPLETED),
            tags = tags,
            isFavorite = isFavorite,
            projectId = projectId
        )
        _tasks.value = listOf(newTask) + _tasks.value
        syncTaskToSupabase(newTask)
        showToast("Tarefa criada: '$title'", ToastType.SUCCESS)
        updateAndSaveMetrics { current ->
            current.copy(tasksToday = current.tasksToday + 1)
        }
    }

    fun toggleTaskFavorite(id: String) {
        _tasks.value = _tasks.value.map {
            if (it.id == id) {
                val newState = !it.isFavorite
                showToast(if (newState) "Adicionado aos favoritos ⭐" else "Removido dos favoritos", ToastType.INFO)
                val updated = it.withFavorite(newState)
                syncTaskToSupabase(updated)
                updated
            } else it
        }
    }

    fun toggleTask(id: String) {
        val task = _tasks.value.find { it.id == id } ?: return
        val wasCompleted = task.isCompleted
        _tasks.value = _tasks.value.map {
            if (it.id == id) {
                val newState = !it.isCompleted
                if (newState) showToast("Tarefa concluída!", ToastType.SUCCESS)
                val updated = it.withCompleted(newState)
                syncTaskToSupabase(updated)
                updated
            } else it
        }
        updateAndSaveMetrics { current ->
            val change = if (!wasCompleted) 1 else -1
            current.copy(completedToday = maxOf(0, current.completedToday + change))
        }
    }

    fun updateTaskStatus(id: String, newStatus: com.example.core.common.TaskStatus) {
        val task = _tasks.value.find { it.id == id } ?: return
        val wasCompleted = task.isCompleted
        val isCompletedNow = (newStatus == com.example.core.common.TaskStatus.COMPLETED)
        
        _tasks.value = _tasks.value.map {
            if (it.id == id) {
                val updated = it.withStatus(newStatus)
                syncTaskToSupabase(updated)
                updated
            } else it
        }
        
        if (wasCompleted != isCompletedNow) {
            updateAndSaveMetrics { current ->
                val change = if (isCompletedNow) 1 else -1
                current.copy(completedToday = maxOf(0, current.completedToday + change))
            }
        }
        showToast("Tarefa movida para ${newStatus.displayName}", ToastType.SUCCESS)
    }

    fun deleteTask(id: String) {
        val task = _tasks.value.find { it.id == id } ?: return
        val wasCompleted = task.isCompleted
        _tasks.value = _tasks.value.filter { it.id != id }
        syncTaskToSupabase(task, isDeletion = true)
        showToast("Tarefa removida", ToastType.INFO)
        updateAndSaveMetrics { current ->
            current.copy(
                tasksToday = maxOf(0, current.tasksToday - 1),
                completedToday = if (wasCompleted) maxOf(0, current.completedToday - 1) else current.completedToday
            )
        }
    }

    // --- Agenda Actions ---
    fun addEvent(title: String, time: String, category: String, description: String) {
        if (title.isBlank() || time.isBlank()) return
        val newEvent = AgendaEvent(
            title = title,
            time = time,
            category = category.ifEmpty { "Compromisso" },
            description = description
        )
        _agendaEvents.value = _agendaEvents.value + newEvent
        showToast("Evento adicionado: '$title'", ToastType.SUCCESS)
    }

    fun deleteEvent(id: String) {
        _agendaEvents.value = _agendaEvents.value.filter { it.id != id }
        showToast("Evento removido", ToastType.INFO)
    }

    // --- Habit Actions ---
    fun addHabit(name: String, icon: String = "⚡", category: String = "Geral", targetDays: Int = 7) {
        if (name.isBlank()) return
        val newHabit = HabitItem(
            name = name,
            streak = 0,
            isCompletedToday = false,
            icon = icon.ifEmpty { "⚡" },
            category = category,
            targetDaysPerWeek = targetDays,
            completedThisWeek = 0,
            isFirestoreSynced = true
        )
        _habits.value = _habits.value + newHabit
        showToast("Hábito '$name' salvo!", ToastType.SUCCESS)

        viewModelScope.launch {
            firestoreHabitRepo.saveHabit(newHabit)
        }
    }

    fun toggleHabit(id: String) {
        var streakChange = 0
        var updatedHabit: HabitItem? = null
        _habits.value = _habits.value.map {
            if (it.id == id) {
                val wasCompleted = it.isCompletedToday
                val newStreak = if (!wasCompleted) it.streak + 1 else maxOf(0, it.streak - 1)
                val newWeekly = if (!wasCompleted) minOf(it.targetDaysPerWeek, it.completedThisWeek + 1) else maxOf(0, it.completedThisWeek - 1)
                streakChange = if (!wasCompleted) 1 else -1
                if (!wasCompleted) showToast("Hábito concluído! Ofensiva de $newStreak dias!", ToastType.SUCCESS)
                val newItem = it.copy(
                    isCompletedToday = !wasCompleted,
                    streak = newStreak,
                    completedThisWeek = newWeekly
                )
                updatedHabit = newItem
                newItem
            } else it
        }
        if (streakChange != 0) {
            updateAndSaveMetrics { current ->
                current.copy(habitStreak = maxOf(0, current.habitStreak + streakChange))
            }
        }
        updatedHabit?.let { habit ->
            viewModelScope.launch {
                firestoreHabitRepo.saveHabit(habit)
            }
        }
    }

    fun deleteHabit(id: String) {
        _habits.value = _habits.value.filter { it.id != id }
        showToast("Hábito removido", ToastType.INFO)
        viewModelScope.launch {
            firestoreHabitRepo.deleteHabit(id)
        }
    }

    // --- Finance Actions ---
    fun addTransaction(title: String, amount: Double, isIncome: Boolean, category: String) {
        if (title.isBlank() || amount <= 0) return
        val df = SimpleDateFormat("dd MMM", Locale.getDefault())
        val newTx = Transaction(
            title = title,
            amount = amount,
            isIncome = isIncome,
            category = category.ifEmpty { if (isIncome) "Receita" else "Despesa" },
            date = df.format(Date())
        )
        _transactions.value = listOf(newTx) + _transactions.value
        showToast("Lançamento financeiro registrado!", ToastType.SUCCESS)
    }

    // --- Health Actions ---
    fun logWater(ml: Int) {
        _waterDrankMl.value = clamp(_waterDrankMl.value + ml, 0, 5000)
        showToast("+${ml}ml de água registrados", ToastType.SUCCESS)
    }

    fun resetWater() {
        _waterDrankMl.value = 0
        showToast("Registro de água reiniciado", ToastType.INFO)
    }

    fun logSleepHours(hours: Float) {
        _sleepHours.value = clamp(_sleepHours.value + hours, 0f, 24f)
        showToast("Horas de sono atualizadas", ToastType.SUCCESS)
    }

    // --- Gemini AI Core Chat & Structured Suggestion Integrations ---

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(content = text, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isChatLoading.value = true
            try {
                val contextPrompt = buildContextPrompt(text)
                val reply = GeminiServiceClient.generateResponse(contextPrompt)
                _chatMessages.value = _chatMessages.value + ChatMessage(content = reply, isUser = false)
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Gemini Chat error", e)
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    content = "Desculpe, ocorreu uma oscilação na rede ao conectar com o Gemini API. Verifique sua GEMINI_API_KEY.",
                    isUser = false
                )
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage("init", "Chat limpo. Como posso te apoiar agora?", false)
        )
    }

    // Local AI Prompt Recommendations for tasks, finances, and habits
    fun generateAITasksSuggestions() {
        viewModelScope.launch {
            _isGlobalLoading.value = true
            _aiTaskSuggestion.value = null
            try {
                val prompt = """
                    Com base no meu status de hábitos:
                    ${_habits.value.joinToString { it.name }}
                    E nas tarefas concluídas. Sugira exatamente 3 tarefas ultra-focadas e práticas para o dia de hoje, organizadas de forma motivadora. Fale em português com no máximo 3 sentenças curtas.
                """.trimIndent()
                val response = GeminiServiceClient.generateResponse(prompt)
                _aiTaskSuggestion.value = response
            } catch (e: Exception) {
                _aiTaskSuggestion.value = "Tente novamente mais tarde. Verifique se o GEMINI_API_KEY foi adicionado."
            } finally {
                _isGlobalLoading.value = false
            }
        }
    }

    fun generateAIFinancialAdvice() {
        viewModelScope.launch {
            _isGlobalLoading.value = true
            _aiFinanceAdvice.value = null
            try {
                val income = _transactions.value.filter { it.isIncome }.sumOf { it.amount }
                val expense = _transactions.value.filter { !it.isIncome }.sumOf { it.amount }
                val prompt = """
                    Analise meu balanço financeiro: Receitas R$$income, Despesas R$$expense. 
                    Dê uma recomendação pragmática e acionável de economia baseada em investimentos ou contenção de custos em no máximo 3 linhas. Fale em português.
                """.trimIndent()
                val response = GeminiServiceClient.generateResponse(prompt)
                _aiFinanceAdvice.value = response
            } catch (e: Exception) {
                _aiFinanceAdvice.value = "Seus lançamentos parecem saudáveis. Configure uma GEMINI_API_KEY para análises personalizadas em tempo real."
            } finally {
                _isGlobalLoading.value = false
            }
        }
    }

    fun generateAIHabitsRecommendation() {
        viewModelScope.launch {
            _isGlobalLoading.value = true
            _aiHabitRecommendation.value = null
            try {
                val prompt = """
                    Análise de saúde:
                    Sono: ${_sleepHours.value} horas registradas.
                    Água consumida: ${_waterDrankMl.value}ml.
                    Recomende um novo ritual de hábitos de alta performance ou uma pequena mudança para amanhã. Use tom minimalista e direto. Em português, até 2 linhas.
                """.trimIndent()
                val response = GeminiServiceClient.generateResponse(prompt)
                _aiHabitRecommendation.value = response
            } catch (e: Exception) {
                _aiHabitRecommendation.value = "Para potencializar seus resultados, tente manter a meta de 8h de sono e beber água logo após acordar."
            } finally {
                _isGlobalLoading.value = false
            }
        }
    }

    fun generateDailyMissionsWithGemini() {
        viewModelScope.launch {
            _isGlobalLoading.value = true
            try {
                val context = com.example.services.gemini.GeminiUserContext(
                    activeTasks = _tasks.value.filter { !it.isCompleted }.map { it.title },
                    pendingHabits = _habits.value.filter { !it.isCompletedToday }.map { it.name },
                    currentGoals = _goals.value.map { it.title },
                    scheduleEvents = _agendaEvents.value.map { "${it.time} - ${it.title}" }
                )
                val geminiRepo = com.example.di.GeminiModule.geminiRepository
                val missions = geminiRepo.generateDailyMissions(context)
                val topMission = missions.firstOrNull()
                if (topMission != null) {
                    showToast("Missão Gerada: '${topMission.title}'", ToastType.SUCCESS)
                }
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Error generating missions with Gemini", e)
            } finally {
                _isGlobalLoading.value = false
            }
        }
    }

    private fun buildContextPrompt(userQuery: String): String {
        val activeTasks = _tasks.value.filter { !it.isCompleted }
        val habitsText = _habits.value.joinToString { "${it.icon} ${it.name} (${if (it.isCompletedToday) "Feito" else "Pendente"})" }
        val budgetIn = _transactions.value.filter { it.isIncome }.sumOf { it.amount }
        val budgetOut = _transactions.value.filter { !it.isIncome }.sumOf { it.amount }

        return """
            Você é o LifeOS AI, um assistente virtual ultra-premium e inteligente que ajuda o usuário com produtividade, finanças, hábitos e rotina de trabalho.
            Fale em Português do Brasil com tom encorajador, minimalista e altamente pragmático.
            
            Informações Atuais do Usuário:
            - Tarefas Pendentes: ${activeTasks.size} (${activeTasks.take(3).joinToString { it.title }})
            - Hábitos de hoje: $habitsText
            - Consumo de água: ${_waterDrankMl.value}ml / Meta 2500ml
            - Sono registrado: ${_sleepHours.value} horas
            - Finanças do mês: Receitas R$$budgetIn | Despesas R$$budgetOut
 
            Mensagem do Usuário: "$userQuery"
            
            Responda de forma sucinta, usando bullet points se necessário. Não adicione saudações repetitivas se o contexto já estiver estabelecido. Dê conselhos acionáveis baseados no status dele.
        """.trimIndent()
    }

    private fun clamp(value: Int, min: Int, max: Int): Int = if (value < min) min else if (value > max) max else value
    private fun clamp(value: Float, min: Float, max: Float): Float = if (value < min) min else if (value > max) max else value

    fun addProject(name: String, color: String) {
        val auth = _authState.value
        val userId = if (auth is AuthState.Authenticated) {
            UUID.nameUUIDFromBytes(auth.email.toByteArray()).toString()
        } else {
            "mock-uuid-1234"
        }
        val newProj = Project(
            id = UUID.randomUUID().toString(),
            name = name,
            color = color,
            userId = userId
        )
        // Optimistic local update
        _projects.value = _projects.value + newProj

        viewModelScope.launch {
            try {
                projectRepository.saveProject(newProj)
                if (auth is AuthState.Authenticated) {
                    showToast("Projeto '$name' criado com sucesso!", ToastType.SUCCESS)
                } else {
                    showToast("Projeto '$name' criado com sucesso (Modo Local)!", ToastType.SUCCESS)
                }
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to save project in Repository", e)
            }
        }
    }

    fun updateProject(id: String, name: String, color: String) {
        val auth = _authState.value
        val userId = if (auth is AuthState.Authenticated) {
            UUID.nameUUIDFromBytes(auth.email.toByteArray()).toString()
        } else {
            "mock-uuid-1234"
        }

        val updatedProj = Project(
            id = id,
            name = name,
            color = color,
            userId = userId
        )

        // Optimistic local update
        _projects.value = _projects.value.map { proj ->
            if (proj.id == id) updatedProj else proj
        }

        viewModelScope.launch {
            try {
                projectRepository.saveProject(updatedProj)
                if (auth is AuthState.Authenticated) {
                    showToast("Projeto atualizado!", ToastType.SUCCESS)
                } else {
                    showToast("Projeto atualizado (Modo Local)!", ToastType.SUCCESS)
                }
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to update project in Repository", e)
            }
        }
    }

    fun deleteProject(id: String) {
        val auth = _authState.value
        val userId = if (auth is AuthState.Authenticated) {
            UUID.nameUUIDFromBytes(auth.email.toByteArray()).toString()
        } else {
            "mock-uuid-1234"
        }

        // Optimistic local update
        _projects.value = _projects.value.filter { it.id != id }

        // Unlink tasks belonging to this project
        _tasks.value = _tasks.value.map { task ->
            if (task.projectId == id) {
                val updated = task.copy(projectId = null)
                syncTaskToSupabase(updated)
                updated
            } else task
        }

        viewModelScope.launch {
            try {
                projectRepository.deleteProject(userId, id)
                if (auth is AuthState.Authenticated) {
                    showToast("Projeto removido!", ToastType.SUCCESS)
                } else {
                    showToast("Projeto removido (Modo Local)!", ToastType.SUCCESS)
                }
            } catch (e: Exception) {
                Log.e("LifeOSViewModel", "Failed to delete project in Repository", e)
            }
        }
    }

    fun updateTaskProject(taskId: String, projectId: String?) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                val updated = task.copy(projectId = projectId)
                syncTaskToSupabase(updated)
                updated
            } else task
        }
    }
}

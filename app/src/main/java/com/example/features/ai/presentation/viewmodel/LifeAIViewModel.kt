package com.example.features.ai.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.ChatMessage
import com.example.features.ai.data.repository.AIRepositoryImpl
import com.example.core.ai.repository.AIMemoryItem
import com.example.core.ai.repository.AIPlanItem
import com.example.core.ai.repository.AIRecommendation
import com.example.core.ai.repository.AIRepository
import com.example.core.ai.repository.AIAnalysisReport
import com.example.core.ai.model.UserContext
import com.example.core.ai.model.AIInsight
import com.example.core.ai.model.AIAction
import com.example.core.ai.model.InsightPriority
import com.example.core.ai.model.AIInsightType
import com.example.features.ai.domain.usecase.GenerateDailyPlanUseCase
import com.example.features.ai.domain.usecase.GenerateInsightsUseCase
import com.example.features.dashboard.domain.model.DashboardData
import com.example.core.network.GeminiServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class AIPromptTemplate(
    val title: String,
    val description: String,
    val prompt: String,
    val icon: String,
    val tag: String
)

class LifeAIViewModel(context: Context) : ViewModel() {
    private val repository: AIRepository = AIRepositoryImpl(context)
    
    // Clean Architecture Use Cases
    private val generateDailyPlanUseCase = GenerateDailyPlanUseCase(repository)
    private val generateInsightsUseCase = GenerateInsightsUseCase(repository)

    // --- Chat State ---
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(id = "init", content = "Olá! Eu sou o LifeOS AI. Estou pronto para ajudar você a alcançar alta performance hoje. Posso planejar seu dia, analisar seus hábitos ou sugerir melhorias baseadas nas suas preferências persistentes (memórias)!", isUser = false)
    ))
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // --- Memories State ---
    private val _memories = MutableStateFlow<List<AIMemoryItem>>(emptyList())
    val memories: StateFlow<List<AIMemoryItem>> = _memories.asStateFlow()

    // --- Planner State ---
    private val _dailyPlan = MutableStateFlow<List<AIPlanItem>>(emptyList())
    val dailyPlan: StateFlow<List<AIPlanItem>> = _dailyPlan.asStateFlow()

    private val _isPlanLoading = MutableStateFlow(false)
    val isPlanLoading: StateFlow<Boolean> = _isPlanLoading.asStateFlow()

    // --- Recommendations State ---
    private val _analysisReport = MutableStateFlow<AIAnalysisReport>(AIAnalysisReport())
    val analysisReport: StateFlow<AIAnalysisReport> = _analysisReport.asStateFlow()

    private val _recommendations = MutableStateFlow<List<AIRecommendation>>(emptyList())
    val recommendations: StateFlow<List<AIRecommendation>> = _recommendations.asStateFlow()

    private val _aiInsightsList = MutableStateFlow<List<AIInsight>>(emptyList())
    val aiInsightsList: StateFlow<List<AIInsight>> = _aiInsightsList.asStateFlow()

    private val _isRecLoading = MutableStateFlow(false)
    val isRecLoading: StateFlow<Boolean> = _isRecLoading.asStateFlow()

    // --- Filter State for Personalized Recommendations ---
    private val _selectedFilter = MutableStateFlow("Todas")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    // --- Active Tab ---
    private val _activeTab = MutableStateFlow(0) // 0: Chat, 1: Planner, 2: Recommendations, 3: Memories
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    // --- Predefined Prompt Library ---
    val promptTemplates = listOf(
        AIPromptTemplate(
            title = "\"O que devo fazer agora?\"",
            description = "Consulte a próxima atividade recomendada para o momento.",
            prompt = "O que devo fazer agora?",
            icon = "⏰",
            tag = "Agenda"
        ),
        AIPromptTemplate(
            title = "\"Planeje meu dia\"",
            description = "Gere um plano otimizado com suas tarefas e prioridades.",
            prompt = "Planeje meu dia",
            icon = "📅",
            tag = "Plano"
        ),
        AIPromptTemplate(
            title = "\"Reorganize minha agenda\"",
            description = "Ajuste os horários conforme suas prioridades.",
            prompt = "Reorganize minha agenda",
            icon = "🔄",
            tag = "Reorganizar"
        ),
        AIPromptTemplate(
            title = "\"Estou atrasado?\"",
            description = "Verifique o andamento das suas atividades em relação ao horário atual.",
            prompt = "Estou atrasado?",
            icon = "⚠️",
            tag = "Status"
        ),
        AIPromptTemplate(
            title = "\"Como melhorar meu Life Score?\"",
            description = "Dicas personalizadas para impulsionar sua pontuação.",
            prompt = "Como melhorar meu Life Score?",
            icon = "⭐",
            tag = "Score"
        ),
        AIPromptTemplate(
            title = "\"Quais hábitos devo fazer?\"",
            description = "Sugestões de hábitos de alto impacto.",
            prompt = "Quais hábitos devo fazer?",
            icon = "🔥",
            tag = "Hábitos"
        )
    )

    init {
        loadMemories()
        loadRecommendations()
    }

    fun setActiveTab(index: Int) {
        _activeTab.value = index
    }

    // --- Memory Actions ---
    fun loadMemories() {
        viewModelScope.launch {
            _memories.value = repository.getMemories()
        }
    }

    fun addMemory(fact: String, category: String) {
        if (fact.isBlank()) return
        viewModelScope.launch {
            val newItem = AIMemoryItem(fact = fact, category = category)
            repository.saveMemory(newItem)
            loadMemories()
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemory(id)
            loadMemories()
        }
    }

    // --- Chat Actions ---
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(content = text, isUser = true)
        _chatMessages.value = _chatMessages.value + userMsg

        viewModelScope.launch {
            _isChatLoading.value = true
            try {
                // Incorporate memories into system instruction/prompt context
                val memoryContext = memories.value.joinToString("; ") { it.fact }
                val contextPrompt = if (memoryContext.isNotEmpty()) {
                    """
                        Informações e preferências persistentes do usuário (use isso para contextualizar e personalizar sua resposta):
                        $memoryContext

                        Mensagem do usuário:
                        $text
                    """.trimIndent()
                } else {
                    text
                }

                val reply = GeminiServiceClient.generateResponse(contextPrompt)
                _chatMessages.value = _chatMessages.value + ChatMessage(content = reply, isUser = false)
            } catch (e: Exception) {
                Log.e("LifeAIViewModel", "Error in Gemini Chat", e)
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    content = "Erro ao processar sua pergunta. Verifique suas credenciais do Gemini nas configurações.",
                    isUser = false
                )
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(id = UUID.randomUUID().toString(), content = "Conversa reiniciada. Como posso te apoiar agora?", isUser = false)
        )
    }

    // --- Planner Actions ---
    fun generatePlan(habits: List<String>, tasks: List<String>, dashboardData: DashboardData? = null) {
        viewModelScope.launch {
            _isPlanLoading.value = true
            try {
                if (dashboardData != null) {
                    val userContext = UserContext.from(dashboardData, memories.value)
                    _dailyPlan.value = generateDailyPlanUseCase(userContext)
                } else {
                    _dailyPlan.value = repository.generateDailyPlan(
                        memories = memories.value,
                        habits = habits,
                        tasks = tasks
                    )
                }
            } catch (e: Exception) {
                Log.e("LifeAIViewModel", "Error generating plan", e)
            } finally {
                _isPlanLoading.value = false
            }
        }
    }

    // --- Recommendations Actions ---
    fun loadRecommendations(dashboardData: DashboardData? = null) {
        viewModelScope.launch {
            _isRecLoading.value = true
            try {
                if (dashboardData != null) {
                    val userContext = UserContext.from(dashboardData, memories.value)
                    
                    // Run the Use Cases to produce domain models
                    val domainInsights = generateInsightsUseCase(userContext)
                    _aiInsightsList.value = domainInsights
                    
                    // Also run the repository for backward compatibility & direct recommendations
                    val report = repository.runLifeAIEngine(dashboardData, memories.value)
                    _analysisReport.value = report
                    _recommendations.value = report.recommendations
                } else {
                    val recs = repository.getRecommendations(memories.value)
                    _recommendations.value = recs
                    _analysisReport.value = AIAnalysisReport(
                        analysisText = "Seu LifeAI Engine está pronto para analisar seu progresso em tempo real.",
                        insights = listOf(
                            "Consistência: Mantenha sua sequência diária de hábitos para enriquecer as análises.",
                            "Equilíbrio: Monitore suas despesas e conclua tarefas de alta prioridade hoje."
                        ),
                        recommendations = recs
                    )
                    
                    _aiInsightsList.value = recs.mapIndexed { index, rec ->
                        AIInsight(
                            id = "rec_insight_$index",
                            title = rec.title,
                            message = rec.description,
                            type = when (rec.type) {
                                com.example.core.ai.repository.RecommendationType.HABIT -> AIInsightType.HABIT
                                com.example.core.ai.repository.RecommendationType.TASK -> AIInsightType.TASK
                                com.example.core.ai.repository.RecommendationType.FINANCE -> AIInsightType.FINANCE
                                com.example.core.ai.repository.RecommendationType.HEALTH -> AIInsightType.HEALTH
                                com.example.core.ai.repository.RecommendationType.OPTIMAL_TIME -> AIInsightType.CALENDAR
                                com.example.core.ai.repository.RecommendationType.PRODUCTIVITY -> AIInsightType.PRODUCTIVITY
                            },
                            priority = when (rec.type) {
                                com.example.core.ai.repository.RecommendationType.OPTIMAL_TIME -> InsightPriority.HIGH
                                else -> InsightPriority.MEDIUM
                            },
                            action = AIAction(
                                id = "rec_action_$index",
                                label = rec.actionText,
                                type = rec.type.name,
                                payloadTitle = rec.title,
                                payloadIcon = rec.icon
                            ),
                            createdAt = System.currentTimeMillis()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("LifeAIViewModel", "Error loading recommendations", e)
            } finally {
                _isRecLoading.value = false
            }
        }
    }

    fun loadPersonalizedSuggestions(
        schedule: List<com.example.core.common.AgendaEvent>,
        tasks: List<com.example.core.common.TaskItem>,
        habits: List<com.example.core.common.HabitItem>,
        goals: List<com.example.core.common.GoalItem>
    ) {
        viewModelScope.launch {
            _isRecLoading.value = true
            try {
                val report = repository.generatePersonalizedSuggestions(
                    schedule = schedule,
                    tasks = tasks,
                    habits = habits,
                    goals = goals,
                    memories = memories.value
                )
                _analysisReport.value = report
                _recommendations.value = report.recommendations
                _aiInsightsList.value = report.recommendations.mapIndexed { index, rec ->
                    AIInsight(
                        id = "pers_insight_$index",
                        title = rec.title,
                        message = rec.description,
                        type = when (rec.type) {
                            com.example.core.ai.repository.RecommendationType.HABIT -> AIInsightType.HABIT
                            com.example.core.ai.repository.RecommendationType.TASK -> AIInsightType.TASK
                            com.example.core.ai.repository.RecommendationType.FINANCE -> AIInsightType.FINANCE
                            com.example.core.ai.repository.RecommendationType.HEALTH -> AIInsightType.HEALTH
                            com.example.core.ai.repository.RecommendationType.OPTIMAL_TIME -> AIInsightType.CALENDAR
                            com.example.core.ai.repository.RecommendationType.PRODUCTIVITY -> AIInsightType.PRODUCTIVITY
                        },
                        priority = when (rec.type) {
                            com.example.core.ai.repository.RecommendationType.OPTIMAL_TIME -> InsightPriority.HIGH
                            com.example.core.ai.repository.RecommendationType.PRODUCTIVITY -> InsightPriority.MEDIUM
                            else -> InsightPriority.MEDIUM
                        },
                        action = AIAction(
                            id = "pers_action_$index",
                            label = rec.actionText,
                            type = rec.type.name,
                            payloadTitle = rec.title,
                            payloadIcon = rec.icon
                        ),
                        createdAt = System.currentTimeMillis()
                    )
                }
            } catch (e: Exception) {
                Log.e("LifeAIViewModel", "Error generating personalized suggestions", e)
            } finally {
                _isRecLoading.value = false
            }
        }
    }
}

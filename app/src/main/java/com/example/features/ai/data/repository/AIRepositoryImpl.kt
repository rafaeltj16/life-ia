package com.example.features.ai.data.repository

import android.content.Context
import com.example.core.ai.repository.AIMemoryItem
import com.example.core.ai.repository.AIPlanItem
import com.example.core.ai.repository.AIRecommendation
import com.example.core.ai.repository.AIRepository
import com.example.core.ai.repository.RecommendationType
import com.example.core.ai.repository.AIAnalysisReport
import com.example.features.dashboard.domain.model.DashboardData
import com.example.features.ai.data.remote.GeminiDataSource
import com.example.core.ai.prompt.PromptBuilder
import com.example.core.ai.engine.AIRulesEngine
import com.example.core.ai.model.UserContext
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AIRepositoryImpl(
    private val context: Context?,
    private val rulesEngine: AIRulesEngine
) : AIRepository {

    constructor(context: Context) : this(context, AIRulesEngine())

    constructor(rulesEngine: AIRulesEngine) : this(null, rulesEngine)

    private val geminiDataSource = GeminiDataSource()
    private val sharedPrefs = context?.getSharedPreferences("lifeos_ai_prefs", Context.MODE_PRIVATE)
    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val memoryAdapter = moshi.adapter<List<AIMemoryItem>>(
        Types.newParameterizedType(List::class.java, AIMemoryItem::class.java)
    )

    private fun getDefaultMemories(): List<AIMemoryItem> {
        return listOf(
            AIMemoryItem(fact = "Prefiro fazer exercícios no final do dia", category = "Saúde"),
            AIMemoryItem(fact = "Trabalho como desenvolvedor freelancer", category = "Trabalho"),
            AIMemoryItem(fact = "Minha principal meta financeira é investir R$ 1.000 por mês", category = "Financeiro"),
            AIMemoryItem(fact = "Quero ler pelo menos 15 páginas de livros antes de dormir", category = "Hábito")
        )
    }

    override suspend fun getMemories(): List<AIMemoryItem> = withContext(Dispatchers.IO) {
        val json = sharedPrefs?.getString("ai_memories", null)
        if (json.isNullOrBlank()) {
            val defaults = getDefaultMemories()
            saveMemoriesList(defaults)
            return@withContext defaults
        }
        return@withContext try {
            memoryAdapter.fromJson(json) ?: getDefaultMemories()
        } catch (e: Exception) {
            getDefaultMemories()
        }
    }

    override suspend fun saveMemory(memory: AIMemoryItem) = withContext(Dispatchers.IO) {
        val current = getMemories().toMutableList()
        val index = current.indexOfFirst { it.id == memory.id }
        if (index != -1) {
            current[index] = memory
        } else {
            current.add(memory)
        }
        saveMemoriesList(current)
    }

    override suspend fun deleteMemory(id: String) = withContext(Dispatchers.IO) {
        val current = getMemories().filter { it.id != id }
        saveMemoriesList(current)
    }

    private fun saveMemoriesList(list: List<AIMemoryItem>) {
        val json = memoryAdapter.toJson(list)
        sharedPrefs?.edit()?.putString("ai_memories", json)?.apply()
    }

    override suspend fun generateDailyPlan(
        memories: List<AIMemoryItem>,
        habits: List<String>,
        tasks: List<String>
    ): List<AIPlanItem> = withContext(Dispatchers.IO) {
        val prompt = PromptBuilder.buildDailyPlanPrompt(memories, habits, tasks)

        try {
            val response = geminiDataSource.generateResponse(prompt)
            val parsed = parsePlanItems(response)
            if (parsed.isNotEmpty()) {
                return@withContext parsed
            }
        } catch (e: Exception) {
            // Fallback parsing or error
        }

        // Return a high-quality fallback plan if API fails or returns unstructured text
        return@withContext getFallbackPlan(memories)
    }

    override suspend fun getRecommendations(memories: List<AIMemoryItem>): List<AIRecommendation> = withContext(Dispatchers.IO) {
        val prompt = PromptBuilder.buildRecommendationsPrompt(memories)

        try {
            val response = geminiDataSource.generateResponse(prompt)
            val parsed = parseRecommendations(response)
            if (parsed.isNotEmpty()) {
                return@withContext parsed
            }
        } catch (e: Exception) {
            // Fallback
        }

        return@withContext getFallbackRecommendations(memories)
    }

    private fun parsePlanItems(rawText: String): List<AIPlanItem> {
        val items = mutableListOf<AIPlanItem>()
        val lines = rawText.split("\n")
        for (line in lines) {
            if (line.isBlank() || !line.contains("|")) continue
            val parts = line.split("|").map { it.trim() }
            if (parts.size >= 5) {
                items.add(
                    AIPlanItem(
                        time = parts[0],
                        activity = parts[1],
                        category = parts[2],
                        priority = parts[3],
                        icon = parts[4]
                    )
                )
            }
        }
        return items
    }

    private fun parseRecommendations(rawText: String): List<AIRecommendation> {
        val list = mutableListOf<AIRecommendation>()
        val lines = rawText.split("\n")
        for (line in lines) {
            if (line.isBlank() || !line.contains("|")) continue
            val parts = line.split("|").map { it.trim() }
            if (parts.size >= 4) {
                val categoryStr = parts[0]
                val title = parts[1]
                val desc = parts[2]
                val emoji = parts[3]

                val type = when (categoryStr.lowercase()) {
                    "hábito", "habito" -> RecommendationType.HABIT
                    "tarefa", "task" -> RecommendationType.TASK
                    "financeiro", "finanças", "financas" -> RecommendationType.FINANCE
                    else -> RecommendationType.HEALTH
                }

                list.add(
                    AIRecommendation(
                        title = title,
                        description = desc,
                        category = categoryStr,
                        icon = emoji,
                        type = type
                    )
                )
            }
        }
        return list
    }

    private fun getFallbackPlan(memories: List<AIMemoryItem>): List<AIPlanItem> {
        return listOf(
            AIPlanItem(time = "08:30", activity = "Projeto Dashboard", category = "Trabalho", priority = "Alta", icon = "💻"),
            AIPlanItem(time = "09:45", activity = "Responder mensagens", category = "Comunicação", priority = "Média", icon = "✉️"),
            AIPlanItem(time = "10:20", activity = "Água", category = "Saúde", priority = "Média", icon = "💧"),
            AIPlanItem(time = "11:00", activity = "Academia", category = "Exercício", priority = "Alta", icon = "🏋️‍♂️"),
            AIPlanItem(time = "13:30", activity = "Landing Page", category = "Trabalho", priority = "Alta", icon = "🖥️"),
            AIPlanItem(time = "15:00", activity = "Leitura", category = "Hábito", priority = "Baixa", icon = "📚")
        )
    }

    private fun getFallbackRecommendations(memories: List<AIMemoryItem>): List<AIRecommendation> {
        return listOf(
            AIRecommendation(
                title = "Beber Copo D'Água",
                description = "Beber 300ml de água logo ao acordar para despertar o corpo.",
                category = "Hábito",
                icon = "💧",
                type = RecommendationType.HABIT
            ),
            AIRecommendation(
                title = "Revisão Semanal de Finanças",
                description = "Defina 10 minutos no domingo para lançar suas despesas no LifeOS.",
                category = "Tarefa",
                icon = "💸",
                type = RecommendationType.TASK
            ),
            AIRecommendation(
                title = "Investimento Automático",
                description = "Configure uma transferência mensal de 10% da renda direto para corretora.",
                category = "Financeiro",
                icon = "📈",
                type = RecommendationType.FINANCE
            ),
            AIRecommendation(
                title = "Almço Consciente",
                description = "Fazer refeições longe das telas para melhorar a digestão e foco.",
                category = "Saúde",
                icon = "🥗",
                type = RecommendationType.HEALTH
            )
        )
    }

    override suspend fun runLifeAIEngine(
        dashboardData: DashboardData,
        memories: List<AIMemoryItem>
    ): AIAnalysisReport = withContext(Dispatchers.IO) {
        val completedTasks = dashboardData.todayTasks.count { it.isCompleted }
        val completedHabits = dashboardData.todayHabits.count { it.isCompletedToday }
        
        val prompt = PromptBuilder.buildLifeAIEnginePrompt(dashboardData, memories)
        
        try {
            val response = geminiDataSource.generateResponse(prompt)
            
            var analysisText = ""
            val insights = mutableListOf<String>()
            val recommendations = mutableListOf<AIRecommendation>()
            
            // Extract analysis
            val analysisStartIdx = response.indexOf("[ANALYSIS_START]")
            val analysisEndIdx = response.indexOf("[ANALYSIS_END]")
            if (analysisStartIdx != -1 && analysisEndIdx != -1 && analysisEndIdx > analysisStartIdx) {
                analysisText = response.substring(analysisStartIdx + "[ANALYSIS_START]".length, analysisEndIdx).trim()
            }
            
            // Extract insights
            val insightsStartIdx = response.indexOf("[INSIGHTS_START]")
            val insightsEndIdx = response.indexOf("[INSIGHTS_END]")
            if (insightsStartIdx != -1 && insightsEndIdx != -1 && insightsEndIdx > insightsStartIdx) {
                val rawInsights = response.substring(insightsStartIdx + "[INSIGHTS_START]".length, insightsEndIdx).trim()
                rawInsights.split("\n").forEach { line ->
                    if (line.isNotBlank()) {
                        val cleaned = line.trim().removePrefix("-").removePrefix("*").trim()
                        if (cleaned.isNotBlank()) {
                            insights.add(cleaned)
                        }
                    }
                }
            }
            
            // Extract recommendations
            val recsStartIdx = response.indexOf("[RECOMMENDATIONS_START]")
            val recsEndIdx = response.indexOf("[RECOMMENDATIONS_END]")
            if (recsStartIdx != -1 && recsEndIdx != -1 && recsEndIdx > recsStartIdx) {
                val rawRecs = response.substring(recsStartIdx + "[RECOMMENDATIONS_START]".length, recsEndIdx).trim()
                val parsedRecs = parseRecommendations(rawRecs)
                if (parsedRecs.isNotEmpty()) {
                    recommendations.addAll(parsedRecs)
                }
            }
            
            if (analysisText.isNotBlank() && insights.isNotEmpty() && recommendations.isNotEmpty()) {
                return@withContext AIAnalysisReport(
                    analysisText = analysisText,
                    insights = insights.take(3),
                    recommendations = recommendations.take(4)
                )
            }
        } catch (e: Exception) {
            // Ignore & fallback
        }
        
        // Dynamic Fallback based on user data
        val fallbackAnalysis = if (completedHabits > 0 || completedTasks > 0) {
            "Excelente trabalho hoje! Você já concluiu $completedHabits hábitos e $completedTasks tarefas. O seu nível ${dashboardData.level} reflete seu compromisso com a alta performance. Continue mantendo esse ritmo consistente!"
        } else {
            "O dia está apenas começando e você tem todo o potencial para torná-lo produtivo. Foque na sua rotina matinal e conclua sua primeira tarefa para dar tração ao seu dia e acumular mais XP."
        }
        
        val fallbackInsights = listOf(
            "Consistência: Sua sequência atual é de ${dashboardData.streak} dias de hábitos ativos.",
            "Equilíbrio Financeiro: Seu saldo atual é de R$ ${dashboardData.financeSummary.balance}. Evite compras impulsivas hoje.",
            "Deep Work: Recomenda-se bloquear 90 minutos para focar exclusivamente em projetos pendentes."
        )
        
        val fallbackRecommendations = getFallbackRecommendations(memories)
        
        return@withContext AIAnalysisReport(
            analysisText = fallbackAnalysis,
            insights = fallbackInsights,
            recommendations = fallbackRecommendations
        )
    }

    override suspend fun generateInsights(
        dashboard: com.example.core.ai.model.DashboardData
    ): List<com.example.core.ai.model.AIInsight> = withContext(Dispatchers.Default) {
        rulesEngine.generateInsights(dashboard)
    }

    override suspend fun generatePersonalizedSuggestions(
        schedule: List<com.example.core.common.AgendaEvent>,
        tasks: List<com.example.core.common.TaskItem>,
        habits: List<com.example.core.common.HabitItem>,
        goals: List<com.example.core.common.GoalItem>,
        memories: List<AIMemoryItem>
    ): AIAnalysisReport = withContext(Dispatchers.Default) {
        com.example.core.ai.engine.PersonalizedAISuggestionEngine.generateSuggestions(
            schedule = schedule,
            tasks = tasks,
            habits = habits,
            goals = goals,
            memories = memories
        )
    }
}

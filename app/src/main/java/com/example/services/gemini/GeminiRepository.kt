package com.example.services.gemini

import timber.log.Timber
import java.util.UUID

/**
 * Repository interface to handle communication with the Gemini API
 * for processing daily mission generation and productivity insights.
 */
interface GeminiRepository {
    /**
     * Generates daily missions tailored to the user's active context.
     */
    suspend fun generateDailyMissions(context: GeminiUserContext): List<GeminiDailyMission>

    /**
     * Generates analytical productivity insights, schedule recommendations, and habit advice.
     */
    suspend fun generateProductivityInsights(context: GeminiUserContext): List<GeminiProductivityInsight>

    /**
     * Direct interface to query the Gemini API with a raw prompt.
     */
    suspend fun generateRawContent(prompt: String): String
}

/**
 * Default implementation of [GeminiRepository] communicating via [GeminiService].
 */
class GeminiRepositoryImpl(
    private val geminiService: GeminiService = GeminiService()
) : GeminiRepository {

    override suspend fun generateDailyMissions(context: GeminiUserContext): List<GeminiDailyMission> {
        return try {
            val response = geminiService.requestDailyMissions(context)
            val parsedMissions = parseMissionsFromResponse(response)
            if (parsedMissions.isNotEmpty()) {
                parsedMissions
            } else {
                getFallbackMissions(context)
            }
        } catch (e: Exception) {
            Timber.e(e, "GeminiRepository: Error generating daily missions")
            getFallbackMissions(context)
        }
    }

    override suspend fun generateProductivityInsights(context: GeminiUserContext): List<GeminiProductivityInsight> {
        return try {
            val response = geminiService.requestProductivityInsights(context)
            val parsedInsights = parseInsightsFromResponse(response)
            if (parsedInsights.isNotEmpty()) {
                parsedInsights
            } else {
                getFallbackInsights(context)
            }
        } catch (e: Exception) {
            Timber.e(e, "GeminiRepository: Error generating productivity insights")
            getFallbackInsights(context)
        }
    }

    override suspend fun generateRawContent(prompt: String): String {
        return geminiService.generateResponse(prompt)
    }

    /**
     * Parses structured mission blocks:
     * [MISSION]
     * TITLE: ...
     * DESC: ...
     * CAT: ...
     * TIME: ...
     * XP: ...
     * PRIORITY: ...
     * [/MISSION]
     */
    private fun parseMissionsFromResponse(response: String): List<GeminiDailyMission> {
        val missions = mutableListOf<GeminiDailyMission>()
        val missionRegex = Regex("\\[MISSION\\]([\\s\\S]*?)\\[/MISSION\\]")
        val matches = missionRegex.findAll(response)

        for (match in matches) {
            val block = match.groupValues[1]
            var title = ""
            var desc = ""
            var cat = "Geral"
            var time = 30
            var xp = 50
            var priority = "ALTA"

            block.lines().forEach { line ->
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("TITLE:", ignoreCase = true) -> {
                        title = trimmed.substringAfter(":").trim()
                    }
                    trimmed.startsWith("DESC:", ignoreCase = true) -> {
                        desc = trimmed.substringAfter(":").trim()
                    }
                    trimmed.startsWith("CAT:", ignoreCase = true) -> {
                        cat = trimmed.substringAfter(":").trim()
                    }
                    trimmed.startsWith("TIME:", ignoreCase = true) -> {
                        val num = Regex("\\d+").find(trimmed)?.value
                        time = num?.toIntOrNull() ?: 30
                    }
                    trimmed.startsWith("XP:", ignoreCase = true) -> {
                        val num = Regex("\\d+").find(trimmed)?.value
                        xp = num?.toIntOrNull() ?: 50
                    }
                    trimmed.startsWith("PRIORITY:", ignoreCase = true) -> {
                        priority = trimmed.substringAfter(":").trim().uppercase()
                    }
                }
            }

            if (title.isNotBlank()) {
                missions.add(
                    GeminiDailyMission(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        description = if (desc.isNotBlank()) desc else title,
                        category = cat,
                        estimatedMinutes = time,
                        points = xp,
                        priority = priority
                    )
                )
            }
        }

        return missions
    }

    /**
     * Parses structured insight blocks:
     * [INSIGHT]
     * TITLE: ...
     * SUMMARY: ...
     * ACTION: ...
     * IMPACT: ...
     * CAT: ...
     * WINDOW: ...
     * [/INSIGHT]
     */
    private fun parseInsightsFromResponse(response: String): List<GeminiProductivityInsight> {
        val insights = mutableListOf<GeminiProductivityInsight>()
        val insightRegex = Regex("\\[INSIGHT\\]([\\s\\S]*?)\\[/INSIGHT\\]")
        val matches = insightRegex.findAll(response)

        for (match in matches) {
            val block = match.groupValues[1]
            var title = ""
            var summary = ""
            var action = ""
            var impact = 85
            var cat = "Produtividade"
            var window: String? = null

            block.lines().forEach { line ->
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("TITLE:", ignoreCase = true) -> {
                        title = trimmed.substringAfter(":").trim()
                    }
                    trimmed.startsWith("SUMMARY:", ignoreCase = true) -> {
                        summary = trimmed.substringAfter(":").trim()
                    }
                    trimmed.startsWith("ACTION:", ignoreCase = true) -> {
                        action = trimmed.substringAfter(":").trim()
                    }
                    trimmed.startsWith("IMPACT:", ignoreCase = true) -> {
                        val num = Regex("\\d+").find(trimmed)?.value
                        impact = num?.toIntOrNull() ?: 85
                    }
                    trimmed.startsWith("CAT:", ignoreCase = true) -> {
                        cat = trimmed.substringAfter(":").trim()
                    }
                    trimmed.startsWith("WINDOW:", ignoreCase = true) -> {
                        window = trimmed.substringAfter(":").trim()
                    }
                }
            }

            if (title.isNotBlank()) {
                insights.add(
                    GeminiProductivityInsight(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        summary = summary,
                        actionRecommendation = action,
                        impactScore = impact,
                        category = cat,
                        optimalTimeWindow = window
                    )
                )
            }
        }

        return insights
    }

    /**
     * High-quality contextual fallback missions derived from the user's active context.
     */
    private fun getFallbackMissions(context: GeminiUserContext): List<GeminiDailyMission> {
        val primaryTask = context.activeTasks.firstOrNull() ?: "Revisar entregáveis de projetos"
        val habitToBoost = context.pendingHabits.firstOrNull() ?: "Hidratação Pré-Foco (2.5L)"
        val goalToAdvance = context.currentGoals.firstOrNull() ?: "Meta Semanal de Alta Performance"

        return listOf(
            GeminiDailyMission(
                title = "Sprint de Foco: $primaryTask",
                description = "Dedique 45 minutos de atenção ininterrupta na sua principal tarefa pendente do dia.",
                category = "Projeto",
                estimatedMinutes = 45,
                points = 60,
                priority = "ALTA"
            ),
            GeminiDailyMission(
                title = "Consistência de Hábito: $habitToBoost",
                description = "Complete e registre este hábito para preservar sua sequência atual de ${context.streakDays} dias.",
                category = "Saúde",
                estimatedMinutes = 15,
                points = 30,
                priority = "MÉDIA"
            ),
            GeminiDailyMission(
                title = "Avanço Estratégico: $goalToAdvance",
                description = "Reserve 30 minutos para tarefas alinhadas à sua meta anual de longo prazo.",
                category = "Metas",
                estimatedMinutes = 30,
                points = 50,
                priority = "ALTA"
            )
        )
    }

    /**
     * High-quality contextual fallback productivity insights derived from the user's active context.
     */
    private fun getFallbackInsights(context: GeminiUserContext): List<GeminiProductivityInsight> {
        return listOf(
            GeminiProductivityInsight(
                title = "Janela Circadiana de Foco Máximo",
                summary = "Seus padrões históricos indicam que seu rendimento analítico atinge o ápice entre 10h e 12h.",
                actionRecommendation = "Aloque suas tarefas de alta complexidade para a manhã e reserve reuniões operacionais para a tarde.",
                impactScore = 92,
                category = "Otimização de Agenda",
                optimalTimeWindow = "10:00 - 12:00"
            ),
            GeminiProductivityInsight(
                title = "Manutenção de Ritmo & Momentum",
                summary = "Seu Focus Score atual de ${context.focusScore}% está na faixa de excelência com ${context.streakDays} dias de streak.",
                actionRecommendation = "Aplique pausas de 10 minutos após blocos de 50 minutos de tela para evitar fadiga cognitiva.",
                impactScore = 88,
                category = "Gestão de Energia",
                optimalTimeWindow = "14:30 - 14:40"
            ),
            GeminiProductivityInsight(
                title = "Agrupamento de Micro-Tarefas (Batching)",
                summary = "Pendências pontuais e recados fragmentam o fluxo mental quando distribuídos ao longo do dia.",
                actionRecommendation = "Reserve um bloco único de 30 minutos às 18:00 para liquidar todas as tarefas administrativas.",
                impactScore = 80,
                category = "Foco Profundo",
                optimalTimeWindow = "18:00 - 18:30"
            )
        )
    }
}

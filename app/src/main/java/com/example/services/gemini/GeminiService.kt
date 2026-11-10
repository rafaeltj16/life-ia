package com.example.services.gemini

import com.example.core.network.GeminiServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Service class in 'services/gemini' to handle communication with the Gemini API.
 * Encapsulates prompt preparation, API execution, and response retrieval for daily
 * mission generation and productivity insights.
 */
class GeminiService(
    private val client: GeminiServiceClient = GeminiServiceClient
) {

    /**
     * Sends a raw prompt to the Gemini API and returns the textual response.
     */
    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            client.generateResponse(prompt)
        } catch (e: Exception) {
            Timber.e(e, "GeminiService: Failed to communicate with Gemini API")
            "Erro ao comunicar com a API do Gemini: ${e.localizedMessage}"
        }
    }

    /**
     * Generates structured daily missions using Gemini API based on user context.
     */
    suspend fun requestDailyMissions(context: GeminiUserContext): String = withContext(Dispatchers.IO) {
        val prompt = buildString {
            appendLine("Você é o assistente inteligente LifeOS AI Copilot.")
            appendLine("Gere exatamente 3 missões diárias de alta relevância com base nos dados do usuário:")
            if (context.activeTasks.isNotEmpty()) {
                appendLine("• Tarefas Prioritárias: ${context.activeTasks.joinToString(", ")}")
            }
            if (context.pendingHabits.isNotEmpty()) {
                appendLine("• Hábitos Ativos: ${context.pendingHabits.joinToString(", ")}")
            }
            if (context.currentGoals.isNotEmpty()) {
                appendLine("• Metas de Longo Prazo: ${context.currentGoals.joinToString(", ")}")
            }
            if (context.scheduleEvents.isNotEmpty()) {
                appendLine("• Agenda de Hoje: ${context.scheduleEvents.joinToString(", ")}")
            }
            appendLine("• Focus Score Atual: ${context.focusScore}% | Sequência: ${context.streakDays} dias")
            appendLine()
            appendLine("Instruções de formatação estrita:")
            appendLine("Retorne cada missão delimitada por [MISSION] e [/MISSION] com as chaves:")
            appendLine("[MISSION]")
            appendLine("TITLE: <título curto>")
            appendLine("DESC: <descrição de execução prática>")
            appendLine("CAT: <Projeto | Foco | Saúde | Finanças>")
            appendLine("TIME: <tempo estimado em minutos, ex: 30>")
            appendLine("XP: <pontuação XP, ex: 50>")
            appendLine("PRIORITY: <ALTA | MÉDIA | BAIXA>")
            appendLine("[/MISSION]")
        }

        generateResponse(prompt)
    }

    /**
     * Generates structured productivity insights and optimal timing suggestions via Gemini API.
     */
    suspend fun requestProductivityInsights(context: GeminiUserContext): String = withContext(Dispatchers.IO) {
        val prompt = buildString {
            appendLine("Você é o especialista em produtividade e alta performance do LifeOS AI.")
            appendLine("Analise os dados do usuário e gere 3 insights acionáveis com foco em melhoria contínua:")
            if (context.activeTasks.isNotEmpty()) {
                appendLine("• Tarefas: ${context.activeTasks.joinToString(", ")}")
            }
            if (context.scheduleEvents.isNotEmpty()) {
                appendLine("• Eventos e Agenda: ${context.scheduleEvents.joinToString(", ")}")
            }
            if (context.pendingHabits.isNotEmpty()) {
                appendLine("• Hábitos: ${context.pendingHabits.joinToString(", ")}")
            }
            if (context.currentGoals.isNotEmpty()) {
                appendLine("• Metas: ${context.currentGoals.joinToString(", ")}")
            }
            appendLine("• Focus Score: ${context.focusScore}%")
            appendLine()
            appendLine("Formate cada insight delimitado por [INSIGHT] e [/INSIGHT]:")
            appendLine("[INSIGHT]")
            appendLine("TITLE: <título do insight>")
            appendLine("SUMMARY: <diagnóstico e análise do padrão>")
            appendLine("ACTION: <recomendação prática direta>")
            appendLine("IMPACT: <número de 1 a 100 indicando o impacto>")
            appendLine("CAT: <Foco Profundo | Gestão de Energia | Otimização de Agenda | Hábitos>")
            appendLine("WINDOW: <horário ideal sugerido, ex: 11:30 - 12:45>")
            appendLine("[/INSIGHT]")
        }

        generateResponse(prompt)
    }

    companion object {
        private val defaultInstance = GeminiService()

        /**
         * Singleton compatibility accessor.
         */
        suspend fun generateResponse(prompt: String): String {
            return defaultInstance.generateResponse(prompt)
        }
    }
}

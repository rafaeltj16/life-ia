package com.example.core.ai.engine

import com.example.core.ai.model.*
import com.example.core.network.GeminiServiceClient
import java.time.LocalTime
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

data class AIRecommendationJson(
    val title: String,
    val description: String,
    val confidence: Double
)

data class EnrichedBlockJson(
    val title: String,
    val description: String
)

data class EnrichedPlanResponse(
    val recommendations: List<AIRecommendationJson>?,
    val enrichedBlocks: List<EnrichedBlockJson>?,
    val estimatedProductivity: Int?
)

class PlannerEngine {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    suspend fun createPlan(
        mission: DailyMission,
        insights: List<AIInsight>,
        bestFocusHour: Int?
    ): DailyPlan {

        val blocks = mutableListOf<TimeBlock>()

        val startHour = bestFocusHour ?: 9

        // 1. Plano por regras (rápido) - Criação inicial instantânea
        blocks.add(
            TimeBlock(
                start = LocalTime.of(startHour, 0),
                end = LocalTime.of(startHour + 1, 30),
                title = mission.title,
                description = mission.description,
                action = mission.action,
                category = BlockCategory.PROJECT
            )
        )

        insights.take(3).forEachIndexed { index, insight ->
            blocks.add(
                TimeBlock(
                    start = LocalTime.of(startHour + 2 + index, 0),
                    end = LocalTime.of(startHour + 2 + index, 30),
                    title = insight.title,
                    description = insight.message,
                    action = insight.action,
                    category = BlockCategory.TASK
                )
            )
        }

        // Recomendações padrão por regras (fallback/inicial rápido)
        val defaultRecommendations = mutableListOf<AIRecommendation>()
        defaultRecommendations.add(
            AIRecommendation(
                title = "Otimize sua Janela de Foco",
                description = "Seu melhor horário biológico é por volta de ${startHour}h. Dedique esse tempo a tarefas complexas e evite distrações.",
                confidence = 0.95f,
                action = null
            )
        )

        val waterInsight = insights.any { it.title.contains("água", ignoreCase = true) || it.message.contains("água", ignoreCase = true) }
        if (waterInsight) {
            defaultRecommendations.add(
                AIRecommendation(
                    title = "Hidratação Constante",
                    description = "Seu consumo de água está abaixo do ideal hoje. Beba 250ml de água ao final de cada bloco de foco.",
                    confidence = 0.90f,
                    action = null
                )
            )
        } else {
            defaultRecommendations.add(
                AIRecommendation(
                    title = "Intervalos de Recuperação",
                    description = "Após cada bloco de foco, faça uma pausa de 5 minutos para alongar-se e descansar a mente.",
                    confidence = 0.85f,
                    action = null
                )
            )
        }

        val taskInsight = insights.any { it.title.contains("atrasada", ignoreCase = true) || it.message.contains("atrasada", ignoreCase = true) }
        if (taskInsight) {
            defaultRecommendations.add(
                AIRecommendation(
                    title = "Resolva as Pendências",
                    description = "Há tarefas pendentes precisando de atenção. Foque em eliminá-las logo no primeiro bloco.",
                    confidence = 0.88f,
                    action = null
                )
            )
        } else {
            defaultRecommendations.add(
                AIRecommendation(
                    title = "Conserve o Ritmo (Momentum)",
                    description = "Seu ritmo de execução está ótimo. Conclua as rotinas agendadas para manter o bônus de pontuação.",
                    confidence = 0.92f,
                    action = null
                )
            )
        }

        var finalBlocks = blocks.toList()
        var finalRecommendations = defaultRecommendations.toList()
        var finalProductivity = 90

        // 2. Gemini (quando necessário/disponível) - Enriquecimento assíncrono
        val prompt = """
            Você é o Copiloto do LifeOS, um organizador inteligente de produtividade e bem-estar de alta performance.
            Sua tarefa é enriquecer um plano diário de blocos de tempo baseado na missão diária e nos insights do usuário.

            Abaixo estão as informações para planejar:
            - Missão Diária: ${mission.title} - ${mission.description}
            - Insights do Dia:
            ${insights.joinToString("\n") { "  * ${it.title}: ${it.message}" }}
            - Melhor hora de foco: ${startHour}h

            Atualmente, o plano básico tem estes blocos:
            ${blocks.joinToString("\n") { "  * ${it.start} - ${it.end}: ${it.title} (${it.category})" }}

            Por favor, enriqueça este plano retornando os seguintes itens formatados exatamente em formato JSON com a seguinte estrutura (não inclua marcações markdown no início ou fim além do próprio JSON limpo):
            {
              "recommendations": [
                {
                  "title": "Título curto da recomendação",
                  "description": "Explicação breve e acionável de como executar essa recomendação",
                  "confidence": 0.95
                }
              ],
              "enrichedBlocks": [
                {
                  "title": "Título do bloco de tempo mais motivador ou descritivo",
                  "description": "Instruções claras ou objetivos específicos de foco para este bloco de tempo"
                }
              ],
              "estimatedProductivity": 95
            }

            Observações importantes:
            1. Crie exatamente 3 recomendações super acionáveis e motivadoras baseadas nos insights e missão.
            2. Forneça o mesmo número de blocos enriquecidos correspondente à lista de blocos informada (${blocks.size}), na mesma ordem.
            3. Não use linguagem técnica demais. Mantenha um estilo motivador, focado e profissional.
        """.trimIndent()

        val parsed = withTimeoutOrNull(6000) {
            try {
                val aiResponse = GeminiServiceClient.generateResponse(prompt)
                val cleanStr = cleanJson(aiResponse)
                val adapter = moshi.adapter(EnrichedPlanResponse::class.java)
                adapter.fromJson(cleanStr)
            } catch (e: Exception) {
                Timber.e(e, "Falha ao enriquecer o plano com Gemini")
                null
            }
        }

        if (parsed != null) {
            finalProductivity = parsed.estimatedProductivity ?: 90
            
            val enriched = parsed.enrichedBlocks
            if (!enriched.isNullOrEmpty()) {
                finalBlocks = blocks.mapIndexed { index, originalBlock ->
                    val enrichedBlock = enriched.getOrNull(index)
                    if (enrichedBlock != null) {
                        originalBlock.copy(
                            title = enrichedBlock.title.ifBlank { originalBlock.title },
                            description = enrichedBlock.description.ifBlank { originalBlock.description }
                        )
                    } else {
                        originalBlock
                    }
                }
            }

            val recs = parsed.recommendations
            if (!recs.isNullOrEmpty()) {
                finalRecommendations = recs.map { r ->
                    AIRecommendation(
                        title = r.title,
                        description = r.description,
                        confidence = r.confidence.toFloat(),
                        action = null
                    )
                }
            }
        }

        return DailyPlan(
            generatedAt = System.currentTimeMillis(),
            mission = mission,
            blocks = finalBlocks,
            estimatedProductivity = finalProductivity,
            recommendations = finalRecommendations
        )
    }

    private fun cleanJson(raw: String): String {
        var cleaned = raw.trim()
        if (cleaned.startsWith("```")) {
            cleaned = cleaned.removePrefix("```")
            if (cleaned.startsWith("json")) {
                cleaned = cleaned.removePrefix("json")
            }
            if (cleaned.endsWith("```")) {
                cleaned = cleaned.removeSuffix("```")
            }
        }
        return cleaned.trim()
    }
}

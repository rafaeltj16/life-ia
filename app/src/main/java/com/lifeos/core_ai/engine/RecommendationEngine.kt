package com.lifeos.core_ai.engine

import com.lifeos.core_ai.context.*
import com.lifeos.core_ai.model.*
import com.example.core.ai.model.AIRecommendation

class RecommendationEngine {

    fun generate(
        context: UserContextSnapshot,
        dashboard: DashboardSnapshot
    ): List<AIRecommendation> {
        val recommendations = mutableListOf<AIRecommendation>()

        if (context.currentEnergy == EnergyLevel.LOW) {
            recommendations += AIRecommendation(
                title = "Reduza a carga",
                description = "Priorize tarefas rápidas enquanto sua energia está baixa.",
                confidence = 0.91f,
                action = null
            )
        }

        if (context.nextEventMinutes != null &&
            context.nextEventMinutes < 30) {
            recommendations += AIRecommendation(
                title = "Prepare-se para o próximo compromisso",
                description = "Evite iniciar uma tarefa longa agora.",
                confidence = 0.96f,
                action = null
            )
        }

        return recommendations
    }

}

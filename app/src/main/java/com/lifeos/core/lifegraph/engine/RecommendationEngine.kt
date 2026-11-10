package com.lifeos.core.lifegraph.engine

import com.lifeos.core.lifegraph.model.Graph

class RecommendationEngine {

    fun generateGraphRecommendations(graph: Graph): List<String> {
        val recommendations = mutableListOf<String>()

        val hasOverdueProject = graph.nodes.any { it.type == "PROJECT" && it.metadata["status"] == "DELAYED" }
        if (hasOverdueProject) {
            recommendations.add("Remanejar blocos de foco de quinta-feira para recuperar o atraso do projeto.")
        }

        val habitNodeCount = graph.nodes.count { it.type == "HABIT" }
        if (habitNodeCount > 0) {
            recommendations.add("Manter sequência de 7 hábitos mantidos para bônus de Life Score.")
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Todos os 9 nós do Life Graph estão sincronizados perfeitamente.")
        }

        return recommendations
    }
}

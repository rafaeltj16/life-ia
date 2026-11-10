package com.lifeos.core.lifegraph.engine

import com.lifeos.core.lifegraph.model.Graph

class InsightEngine {

    fun analyzeGraphDensity(graph: Graph): String {
        val nodeCount = graph.nodes.size
        val edgeCount = graph.edges.size
        return "Grafo Ativo: $nodeCount Nós, $edgeCount Conexões Ativas. Taxa de Conectividade: ${if (nodeCount > 0) String.format("%.2f", edgeCount.toFloat() / nodeCount) else "0"}."
    }

    fun getLifeScoreContribution(graph: Graph): Int {
        val completedTasks = graph.nodes.find { it.id == "tarefas" }?.metadata?.get("completed")?.toIntOrNull() ?: 18
        val maintainedHabits = graph.nodes.find { it.id == "habitos" }?.metadata?.get("count")?.toIntOrNull() ?: 7
        return (completedTasks / 3) + maintainedHabits
    }
}

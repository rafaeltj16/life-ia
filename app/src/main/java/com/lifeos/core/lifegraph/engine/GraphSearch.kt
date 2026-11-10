package com.lifeos.core.lifegraph.engine

import com.lifeos.core.lifegraph.model.Graph
import com.lifeos.core.lifegraph.model.Node

class GraphSearch {

    fun searchNodes(graph: Graph, query: String): List<Node> {
        if (query.isBlank()) return graph.nodes
        val lowercaseQuery = query.lowercase()
        return graph.nodes.filter {
            it.label.lowercase().contains(lowercaseQuery) ||
                    it.type.lowercase().contains(lowercaseQuery) ||
                    it.detailDescription().lowercase().contains(lowercaseQuery)
        }
    }

    private fun Node.detailDescription(): String {
        return metadata.values.joinToString(" ")
    }
}

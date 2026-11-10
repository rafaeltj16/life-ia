package com.lifeos.core.lifegraph.engine

import com.lifeos.core.lifegraph.model.Edge
import com.lifeos.core.lifegraph.model.Graph
import com.lifeos.core.lifegraph.model.Node

class RelationshipEngine {

    fun findConnectedNodes(graph: Graph, nodeId: String): List<Node> {
        val connectedIds = graph.edges
            .filter { it.sourceNodeId == nodeId || it.targetNodeId == nodeId }
            .map { if (it.sourceNodeId == nodeId) it.targetNodeId else it.sourceNodeId }
            .toSet()

        return graph.nodes.filter { it.id in connectedIds }
    }

    fun findPathBetween(graph: Graph, startNodeId: String, endNodeId: String): List<Edge> {
        val visited = mutableSetOf<String>()
        val queue = ArrayDeque<Pair<String, List<Edge>>>()
        queue.add(startNodeId to emptyList())

        while (queue.isNotEmpty()) {
            val (current, path) = queue.removeFirst()
            if (current == endNodeId) return path
            if (current !in visited) {
                visited.add(current)
                val outgoingEdges = graph.getEdgesFrom(current)
                for (edge in outgoingEdges) {
                    queue.add(edge.targetNodeId to (path + edge))
                }
            }
        }
        return emptyList()
    }
}

package com.lifeos.core.lifegraph.model

data class Graph(
    val id: String,
    val type: GraphType,
    val nodes: List<Node> = emptyList(),
    val edges: List<Edge> = emptyList()
) {
    fun getNode(id: String): Node? = nodes.find { it.id == id }
    fun getEdgesFrom(nodeId: String): List<Edge> = edges.filter { it.sourceNodeId == nodeId }
    fun getEdgesTo(nodeId: String): List<Edge> = edges.filter { it.targetNodeId == nodeId }
}

package com.lifeos.core.lifegraph.engine

import com.lifeos.core.lifegraph.model.Edge
import com.lifeos.core.lifegraph.model.Graph
import com.lifeos.core.lifegraph.model.GraphType
import com.lifeos.core.lifegraph.model.Node

class GraphBuilder {
    private var id: String = "custom_graph"
    private var type: GraphType = GraphType.PERSONAL_LIFE_OS
    private val nodes = mutableListOf<Node>()
    private val edges = mutableListOf<Edge>()

    fun setId(graphId: String) = apply { this.id = graphId }
    fun setType(graphType: GraphType) = apply { this.type = graphType }

    fun addNode(node: Node) = apply {
        nodes.removeAll { it.id == node.id }
        nodes.add(node)
    }

    fun addEdge(edge: Edge) = apply {
        edges.removeAll { it.id == edge.id }
        edges.add(edge)
    }

    fun build(): Graph {
        return Graph(id = id, type = type, nodes = nodes.toList(), edges = edges.toList())
    }
}

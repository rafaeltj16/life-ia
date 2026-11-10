package com.lifeos.core.lifegraph.repository

import com.lifeos.core.lifegraph.model.Edge
import com.lifeos.core.lifegraph.model.Graph
import com.lifeos.core.lifegraph.model.Node
import kotlinx.coroutines.flow.Flow

interface GraphRepository {
    fun getGraph(): Flow<Graph>
    suspend fun addNode(node: Node)
    suspend fun addEdge(edge: Edge)
    suspend fun updateNode(node: Node)
    suspend fun removeNode(nodeId: String)
}

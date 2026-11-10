package com.lifeos.core.lifegraph.repository

import com.lifeos.core.lifegraph.model.Edge
import com.lifeos.core.lifegraph.model.Graph
import com.lifeos.core.lifegraph.model.GraphType
import com.lifeos.core.lifegraph.model.Node
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GraphRepositoryImpl : GraphRepository {

    private val defaultGraph = Graph(
        id = "root_life_graph",
        type = GraphType.PERSONAL_LIFE_OS,
        nodes = listOf(
            Node("user", "Usuário", "USER"),
            Node("engine", "Life Graph Engine", "ENGINE"),
            Node("objetivos", "Objetivos", "GOAL"),
            Node("projetos", "Projetos", "PROJECT"),
            Node("pessoas", "Pessoas", "PERSON"),
            Node("tarefas", "Tarefas", "TASK"),
            Node("calendario", "Calendário", "CALENDAR"),
            Node("conversas", "Conversas", "CONVERSATION"),
            Node("habitos", "Hábitos", "HABIT"),
            Node("focus", "Focus Mode", "FOCUS"),
            Node("notas", "Notas", "NOTE"),
            Node("life_ai", "LifeAI", "AI")
        ),
        edges = listOf(
            Edge("e1", "user", "engine", "DRIVES_ENGINE"),
            Edge("e2", "engine", "objetivos", "HAS_GOAL"),
            Edge("e3", "engine", "projetos", "HAS_PROJECT"),
            Edge("e4", "engine", "pessoas", "HAS_PERSON"),
            Edge("e5", "objetivos", "tarefas", "DECOMPOSE_TO"),
            Edge("e6", "projetos", "calendario", "SCHEDULED_IN"),
            Edge("e7", "pessoas", "conversas", "DISCUSSES"),
            Edge("e8", "tarefas", "habitos", "FEEDS_HABIT"),
            Edge("e9", "calendario", "focus", "ALLOCATES_FOCUS"),
            Edge("e10", "conversas", "notas", "CAPTURED_IN"),
            Edge("e11", "habitos", "life_ai", "FEEDS_AI"),
            Edge("e12", "focus", "life_ai", "FEEDS_AI"),
            Edge("e13", "notas", "life_ai", "FEEDS_AI")
        )
    )

    private val _graphState = MutableStateFlow(defaultGraph)

    override fun getGraph(): Flow<Graph> = _graphState.asStateFlow()

    override suspend fun addNode(node: Node) {
        _graphState.update { current ->
            current.copy(nodes = current.nodes.filterNot { it.id == node.id } + node)
        }
    }

    override suspend fun addEdge(edge: Edge) {
        _graphState.update { current ->
            current.copy(edges = current.edges.filterNot { it.id == edge.id } + edge)
        }
    }

    override suspend fun updateNode(node: Node) {
        addNode(node)
    }

    override suspend fun removeNode(nodeId: String) {
        _graphState.update { current ->
            current.copy(
                nodes = current.nodes.filterNot { it.id == nodeId },
                edges = current.edges.filterNot { it.sourceNodeId == nodeId || it.targetNodeId == nodeId }
            )
        }
    }
}

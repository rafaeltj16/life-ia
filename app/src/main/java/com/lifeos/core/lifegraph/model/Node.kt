package com.lifeos.core.lifegraph.model

data class Node(
    val id: String,
    val label: String,
    val type: String,
    val metadata: Map<String, String> = emptyMap(),
    val weight: Float = 1.0f
) {
    fun toGraphNode(): GraphNode = when (type.uppercase()) {
        "TASK", "TAREFA" -> GraphNode.Task(id)
        "HABIT", "HABITO" -> GraphNode.Habit(id)
        "GOAL", "OBJETIVO" -> GraphNode.Goal(id)
        "PROJECT", "PROJETO" -> GraphNode.Project(id)
        "NOTE", "NOTA" -> GraphNode.Note(id)
        "EVENT", "CALENDAR", "EVENTO" -> GraphNode.Event(id)
        "PERSON", "PESSOA" -> GraphNode.Person(id)
        else -> GraphNode.Task(id)
    }
}

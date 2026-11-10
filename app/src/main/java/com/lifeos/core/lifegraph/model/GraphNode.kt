package com.lifeos.core.lifegraph.model

sealed class GraphNode {
    data class Task(val id: String) : GraphNode()
    data class Habit(val id: String) : GraphNode()
    data class Goal(val id: String) : GraphNode()
    data class Project(val id: String) : GraphNode()
    data class Note(val id: String) : GraphNode()
    data class Event(val id: String) : GraphNode()
    data class Person(val id: String) : GraphNode()
}

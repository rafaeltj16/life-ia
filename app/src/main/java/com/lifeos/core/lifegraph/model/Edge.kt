package com.lifeos.core.lifegraph.model

data class Edge(
    val id: String,
    val sourceNodeId: String,
    val targetNodeId: String,
    val relationshipType: String,
    val weight: Float = 1.0f
) {
    fun toRelationType(): RelationType = try {
        RelationType.valueOf(relationshipType.uppercase())
    } catch (e: Exception) {
        RelationType.RELATED_TO
    }
}

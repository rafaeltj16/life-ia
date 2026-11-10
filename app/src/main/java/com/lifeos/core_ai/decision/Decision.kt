package com.lifeos.core_ai.decision

data class Decision(
    val type: DecisionType,
    val reason: DecisionReason,
    val confidence: Float
)

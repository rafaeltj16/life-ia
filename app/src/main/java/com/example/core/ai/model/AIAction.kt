package com.example.core.ai.model

sealed class AIAction {
    open val id: String = ""
    open val label: String = ""
    open val type: String = ""
    open val payloadTitle: String = ""
    open val payloadIcon: String = "✨"
    open val payloadCategory: String = "IA"

    data class OpenTask(
        val taskId: String
    ) : AIAction()

    data class OpenProject(
        val projectId: String
    ) : AIAction()

    data class CompleteHabit(
        val habitId: String
    ) : AIAction()

    data class StartFocusSession(
        val minutes: Int
    ) : AIAction()

    data class ScheduleTask(
        val taskId: String
    ) : AIAction()

    object OpenDashboard : AIAction()

    // Legacy action subclass to support existing direct instantiation
    data class LegacyAction(
        override val id: String,
        override val label: String,
        override val type: String,
        override val payloadTitle: String,
        override val payloadIcon: String = "✨",
        override val payloadCategory: String = "IA"
    ) : AIAction()

    companion object {
        operator fun invoke(
            id: String,
            label: String,
            type: String,
            payloadTitle: String,
            payloadIcon: String = "✨",
            payloadCategory: String = "IA"
        ): AIAction = LegacyAction(
            id = id,
            label = label,
            type = type,
            payloadTitle = payloadTitle,
            payloadIcon = payloadIcon,
            payloadCategory = payloadCategory
        )
    }
}

package com.lifeos.core.ai.models

sealed class AIAction {
    data class CreateTask(val title: String, val category: String = "Geral") : AIAction()
    data class StartFocusSession(val durationMinutes: Int = 25) : AIAction()
    data class SuggestHabit(val name: String) : AIAction()
    data class ShowMessage(val message: String) : AIAction()
    object RecalculateScore : AIAction()
}

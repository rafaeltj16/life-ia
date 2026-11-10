package com.lifeos.core.ai.orchestrator

import com.lifeos.core.ai.models.AIAction

class AIActionExecutor {
    suspend fun execute(action: AIAction): Boolean {
        return when (action) {
            is AIAction.CreateTask -> true
            is AIAction.StartFocusSession -> true
            is AIAction.SuggestHabit -> true
            is AIAction.ShowMessage -> true
            AIAction.RecalculateScore -> true
        }
    }

    suspend fun executeAll(actions: List<AIAction>): Int {
        var count = 0
        actions.forEach { action ->
            if (execute(action)) count++
        }
        return count
    }
}

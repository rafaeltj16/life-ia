package com.lifeos.core.automation

class AutomationExecutor {
    suspend fun execute(action: AutomationAction): Boolean {
        return when (action) {
            AutomationAction.StartFocusMode -> true
            AutomationAction.CreateDailyMission -> true
            AutomationAction.RecalculateLifeScore -> true
            AutomationAction.SendNotification -> true
            AutomationAction.SyncData -> true
            AutomationAction.AskLifeAI -> true
        }
    }
}

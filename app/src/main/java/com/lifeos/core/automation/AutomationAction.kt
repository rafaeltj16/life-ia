package com.lifeos.core.automation

sealed class AutomationAction {

    object StartFocusMode : AutomationAction()

    object CreateDailyMission : AutomationAction()

    object RecalculateLifeScore : AutomationAction()

    object SendNotification : AutomationAction()

    object SyncData : AutomationAction()

    object AskLifeAI : AutomationAction()

}

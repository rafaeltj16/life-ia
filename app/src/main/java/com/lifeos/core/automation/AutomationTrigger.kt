package com.lifeos.core.automation

sealed class AutomationTrigger {
    data class Time(val hour: Int) : AutomationTrigger()
    data class HabitCompleted(val habitId: String) : AutomationTrigger()
    data class TaskCompleted(val taskId: String) : AutomationTrigger()
    object WakeUp : AutomationTrigger()
    object BedTime : AutomationTrigger()
    object AppOpened : AutomationTrigger()
}

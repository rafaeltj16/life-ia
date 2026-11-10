package com.lifeos.core.automation

class AutomationEngine(
    private val repository: AutomationRepository = InMemoryAutomationRepository(),
    private val executor: AutomationExecutor = AutomationExecutor(),
    private val scheduler: AutomationScheduler = AutomationScheduler()
) {
    suspend fun evaluateAndExecute(trigger: AutomationTrigger): List<AutomationRule> {
        val activeRules = repository.getActiveRules()
        val executedRules = mutableListOf<AutomationRule>()

        for (rule in activeRules) {
            if (matchesTrigger(rule.trigger, trigger)) {
                var allSuccess = true
                for (action in rule.actions) {
                    val success = executor.execute(action)
                    if (!success) {
                        allSuccess = false
                    }
                }
                if (allSuccess) {
                    executedRules.add(rule)
                }
            }
        }
        return executedRules
    }

    private fun matchesTrigger(ruleTrigger: AutomationTrigger, eventTrigger: AutomationTrigger): Boolean {
        return when {
            ruleTrigger is AutomationTrigger.Time && eventTrigger is AutomationTrigger.Time -> {
                ruleTrigger.hour == eventTrigger.hour
            }
            ruleTrigger is AutomationTrigger.HabitCompleted && eventTrigger is AutomationTrigger.HabitCompleted -> {
                ruleTrigger.habitId == eventTrigger.habitId || ruleTrigger.habitId.isBlank()
            }
            ruleTrigger is AutomationTrigger.TaskCompleted && eventTrigger is AutomationTrigger.TaskCompleted -> {
                ruleTrigger.taskId == eventTrigger.taskId || ruleTrigger.taskId.isBlank()
            }
            ruleTrigger is AutomationTrigger.WakeUp && eventTrigger is AutomationTrigger.WakeUp -> true
            ruleTrigger is AutomationTrigger.BedTime && eventTrigger is AutomationTrigger.BedTime -> true
            ruleTrigger is AutomationTrigger.AppOpened && eventTrigger is AutomationTrigger.AppOpened -> true
            else -> false
        }
    }
}

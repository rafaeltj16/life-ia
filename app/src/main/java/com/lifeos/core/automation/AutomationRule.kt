package com.lifeos.core.automation

data class AutomationRule(
    val id: String,
    val name: String,
    val enabled: Boolean,
    val trigger: AutomationTrigger,
    val actions: List<AutomationAction>
)

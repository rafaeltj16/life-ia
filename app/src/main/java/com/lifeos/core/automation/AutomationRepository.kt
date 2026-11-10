package com.lifeos.core.automation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

interface AutomationRepository {
    suspend fun getAllRules(): List<AutomationRule>
    suspend fun getActiveRules(): List<AutomationRule>
    suspend fun saveRule(rule: AutomationRule)
    suspend fun deleteRule(ruleId: String)
    suspend fun updateRuleStatus(ruleId: String, enabled: Boolean)
}

class InMemoryAutomationRepository : AutomationRepository {
    private val rules = MutableStateFlow<List<AutomationRule>>(
        listOf(
            AutomationRule(
                id = UUID.randomUUID().toString(),
                name = "Rotina Matinal",
                enabled = true,
                trigger = AutomationTrigger.WakeUp,
                actions = listOf(AutomationAction.SendNotification, AutomationAction.CreateDailyMission)
            ),
            AutomationRule(
                id = UUID.randomUUID().toString(),
                name = "Lembrete das 8h",
                enabled = true,
                trigger = AutomationTrigger.Time(hour = 8),
                actions = listOf(AutomationAction.RecalculateLifeScore)
            )
        )
    )

    val rulesFlow: Flow<List<AutomationRule>> = rules.asStateFlow()

    override suspend fun getAllRules(): List<AutomationRule> = rules.value

    override suspend fun getActiveRules(): List<AutomationRule> = rules.value.filter { it.enabled }

    override suspend fun saveRule(rule: AutomationRule) {
        val current = rules.value.toMutableList()
        val index = current.indexOfFirst { it.id == rule.id }
        if (index >= 0) {
            current[index] = rule
        } else {
            current.add(rule)
        }
        rules.value = current
    }

    override suspend fun deleteRule(ruleId: String) {
        rules.value = rules.value.filterNot { it.id == ruleId }
    }

    override suspend fun updateRuleStatus(ruleId: String, enabled: Boolean) {
        val current = rules.value.toMutableList()
        val index = current.indexOfFirst { it.id == ruleId }
        if (index >= 0) {
            current[index] = current[index].copy(enabled = enabled)
            rules.value = current
        }
    }
}

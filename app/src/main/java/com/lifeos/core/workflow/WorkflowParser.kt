package com.lifeos.core.workflow

import java.util.UUID

data class ParsedWorkflowSpec(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val category: String,
    val trigger: Trigger,
    val actions: List<Action>,
    val enabled: Boolean = true,
    val metadata: Map<String, String> = emptyMap()
)

class WorkflowParser {

    fun parseIntent(intent: WorkflowIntent, originalMessage: String = ""): ParsedWorkflowSpec {
        val meta = mutableMapOf<String, String>()
        if (originalMessage.isNotBlank()) {
            meta["originalMessage"] = originalMessage
        }
        meta["confidence"] = intent.confidence.toString()
        meta["detectedIntent"] = intent.intentName

        return ParsedWorkflowSpec(
            name = intent.intentName,
            description = "Workflow gerado automaticamente para: ${intent.intentName}",
            category = intent.suggestedCategory,
            trigger = intent.suggestedTrigger,
            actions = intent.suggestedActions,
            enabled = true,
            metadata = meta
        )
    }

    fun parseFromMap(data: Map<String, Any?>): Workflow {
        val id = data["id"] as? String ?: UUID.randomUUID().toString()
        val name = data["name"] as? String ?: "Novo Workflow"
        val description = data["description"] as? String ?: ""
        val category = data["category"] as? String ?: "Geral"
        val isEnabled = data["enabled"] as? Boolean ?: (data["isEnabled"] as? Boolean ?: true)

        val actionsData = (data["actions"] as? List<*>) ?: (data["steps"] as? List<*>) ?: emptyList<Any>()
        val actions = actionsData.mapNotNull { item ->
            when (item) {
                is Action -> item
                is String -> parseActionFromString(item)
                is Map<*, *> -> {
                    val typeStr = (item["type"] ?: item["actionType"] ?: item["name"])?.toString() ?: ""
                    parseActionFromString(typeStr)
                }
                else -> null
            }
        }

        return Workflow(
            id = id,
            name = name,
            description = description,
            category = category,
            actions = actions,
            enabled = isEnabled
        )
    }

    fun parseActionFromString(str: String): Action {
        return when (str.trim().uppercase()) {
            "CALCULATELIFESCORE", "CALCULATE_LIFE_SCORE", "UPDATE_LIFE_SCORE" -> Action.CalculateLifeScore
            "GENERATEMISSION", "GENERATE_MISSION" -> Action.GenerateMission
            "STARTFOCUS", "START_FOCUS", "START_FOCUS_SESSION" -> Action.StartFocus
            "CREATETASK", "CREATE_TASK" -> Action.CreateTask
            "SENDNOTIFICATION", "SEND_NOTIFICATION" -> Action.SendNotification
            "GENERATEWEEKLYREVIEW", "GENERATE_WEEKLY_REVIEW" -> Action.GenerateWeeklyReview
            "SYNCSUPABASE", "SYNC_SUPABASE", "SYNC_CALENDAR" -> Action.SyncSupabase
            "ASKGEMINI", "ASK_GEMINI", "GENERATE_AI_INSIGHT" -> Action.AskGemini
            "UPDATEDASHBOARD", "UPDATE_DASHBOARD", "ATUALIZARDASHBOARD", "ATUALIZAR_DASHBOARD", "ATUALIZAR DASHBOARD" -> Action.UpdateDashboard
            "RECALCULAR_SCORE", "RECALCULARSCORE", "RECALCULATE_SCORE", "RECALCULAR SCORE" -> Action.CalculateLifeScore
            else -> Action.CreateTask
        }
    }

    fun parseTemplate(template: WorkflowTemplate, customParams: Map<String, String> = emptyMap()): Workflow {
        val base = template.defaultWorkflow
        return base.copy(
            id = UUID.randomUUID().toString(),
            name = base.name
        )
    }
}

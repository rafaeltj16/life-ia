package com.lifeos.core.ai.orchestrator

import com.lifeos.core.ai.models.AIResponse

class AIResponseFormatter {
    fun format(response: AIResponse): String {
        val sb = StringBuilder()
        sb.append(response.text)
        if (response.actions.isNotEmpty()) {
            sb.append("\n\nAções recomendadas:")
            response.actions.forEach { action ->
                sb.append("\n• ").append(action::class.simpleName)
            }
        }
        return sb.toString()
    }
}

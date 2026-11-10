package com.lifeos.core_ai.parser

import com.example.core.ai.model.AIRecommendation

class ResponseParser {

    fun parse(
        text: String
    ): AIRecommendation {
        return AIRecommendation(
            title = "Sugestão",
            description = text,
            confidence = 0.9f,
            action = null
        )
    }

}

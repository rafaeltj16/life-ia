package com.lifeos.core_ai.learning

import com.example.core.ai.model.AIRecommendation

class AdaptiveRecommendationEngine(
    private val predictor: ProductivityPredictor
) {

    fun recommend(
        profile: UserBehaviorProfile,
        hour: Int
    ): AIRecommendation {
        val confidence = predictor.predict(
            profile,
            hour
        )
        return AIRecommendation(
            title = "Melhor momento para focar",
            description = "Este horário possui alta probabilidade de produtividade.",
            confidence = confidence,
            action = null
        )
    }

}

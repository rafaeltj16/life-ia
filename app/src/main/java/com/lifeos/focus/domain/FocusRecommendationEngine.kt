package com.lifeos.focus.domain

import com.lifeos.core_ai.learning.UserBehaviorProfile

class FocusRecommendationEngine {

    fun generate(
        profile: UserBehaviorProfile,
        hour: Int
    ): FocusRecommendation {
        return if (hour == profile.preferredFocusHour) {
            FocusRecommendation(
                title = "Excelente momento",
                description = "Este costuma ser seu horário mais produtivo.",
                confidence = 0.96f
            )
        } else {
            FocusRecommendation(
                title = "Momento razoável",
                description = "Você produz melhor em outro horário.",
                confidence = 0.71f
            )
        }
    }
}

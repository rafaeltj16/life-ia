package com.lifeos.core_ai.learning

class ProductivityPredictor {

    fun predict(
        profile: UserBehaviorProfile,
        currentHour: Int
    ): Float {
        val difference =
            kotlin.math.abs(
                profile.preferredFocusHour - currentHour
            )
        return when {
            difference == 0 -> 0.98f
            difference <= 1 -> 0.90f
            difference <= 2 -> 0.80f
            else -> 0.55f
        }
    }

}

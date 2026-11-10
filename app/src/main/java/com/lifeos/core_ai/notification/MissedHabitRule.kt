package com.lifeos.core_ai.notification

import com.lifeos.core_ai.provider.HabitProvider
import java.time.LocalDateTime

class MissedHabitRule(
    private val habitProvider: HabitProvider
) : NotificationRule {

    override suspend fun shouldNotify(): Boolean {
        return habitProvider
            .getCompletedToday()
            .isEmpty()
    }

    override suspend fun build(): SmartNotification {
        return SmartNotification(
            id = "habit",
            title = "Você esqueceu seus hábitos",
            message = "Ainda dá tempo de manter sua sequência.",
            priority = NotificationPriority.HIGH,
            scheduledAt = LocalDateTime.now()
        )
    }

}

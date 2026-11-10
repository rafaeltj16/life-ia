package com.lifeos.core_ai.notification

import com.lifeos.core_ai.decision.DecisionEngine
import com.lifeos.core_ai.memory.MemoryEngine
import com.lifeos.core_ai.prediction.PredictionEngine
import java.time.LocalDateTime

class PredictiveNotificationRule(
    private val decisionEngine: DecisionEngine,
    private val memoryEngine: MemoryEngine,
    private val predictionEngine: PredictionEngine,
    private val hasUserOpenedAppToday: () -> Boolean,
    private val currentTimeProvider: () -> LocalDateTime = { LocalDateTime.now() }
) : NotificationRule {

    override suspend fun shouldNotify(): Boolean {
        val now = currentTimeProvider()
        val isTargetTime = now.hour == 8 && now.minute == 30
        
        if (!isTargetTime) return false
        if (hasUserOpenedAppToday()) return false
        
        val prob = predictionEngine.calculateOpenProbability(now.toLocalTime())
        return prob >= 80
    }

    override suspend fun build(): SmartNotification {
        val periodMessage = predictionEngine.predictFocusPeriod()
        val prob = predictionEngine.calculateOpenProbability(currentTimeProvider().toLocalTime())
        
        return SmartNotification(
            id = "predictive_focus",
            title = "Hora de Focar",
            message = "$periodMessage. Chance de abertura: $prob%. Enviar notificação agora.",
            priority = NotificationPriority.HIGH,
            scheduledAt = currentTimeProvider()
        )
    }
}

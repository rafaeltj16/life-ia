package com.lifeos.core_ai.notification

class NotificationEngine(
    private val rules: List<NotificationRule>
) {

    suspend fun generate():
        List<SmartNotification> {
        val notifications = mutableListOf<SmartNotification>()
        rules.forEach {
            if (it.shouldNotify()) {
                notifications.add(
                    it.build()
                )
            }
        }
        return notifications
            .sortedByDescending {
                it.priority.ordinal
            }
    }

}

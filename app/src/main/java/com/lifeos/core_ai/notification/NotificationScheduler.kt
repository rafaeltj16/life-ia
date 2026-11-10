package com.lifeos.core_ai.notification

class NotificationScheduler(
    private val notificationEngine: NotificationEngine
) {

    suspend fun execute() {
        val notifications =
            notificationEngine.generate()
        notifications.forEach {
            // WorkManager.enqueue()
        }
    }

}

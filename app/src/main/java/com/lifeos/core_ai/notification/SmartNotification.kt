package com.lifeos.core_ai.notification

import java.time.LocalDateTime

data class SmartNotification(
    val id: String,
    val title: String,
    val message: String,
    val priority: NotificationPriority,
    val scheduledAt: LocalDateTime,
    val deepLink: String? = null
)

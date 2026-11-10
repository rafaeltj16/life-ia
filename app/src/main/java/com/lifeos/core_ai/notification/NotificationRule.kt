package com.lifeos.core_ai.notification

interface NotificationRule {

    suspend fun shouldNotify(): Boolean

    suspend fun build(): SmartNotification

}

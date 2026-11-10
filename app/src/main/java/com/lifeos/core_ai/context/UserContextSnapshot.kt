package com.lifeos.core_ai.context

import java.time.LocalDateTime

data class UserContextSnapshot(
    val timestamp: LocalDateTime,
    val batteryLevel: Int,
    val isCharging: Boolean,
    val networkAvailable: Boolean,
    val currentLocation: String?,
    val weather: WeatherInfo?,
    val currentEnergy: EnergyLevel,
    val nextEventMinutes: Int?,
    val availableFocusMinutes: Int,
    val currentMomentum: Int
)

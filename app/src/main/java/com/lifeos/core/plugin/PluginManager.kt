package com.lifeos.core.plugin

enum class PluginType(val displayName: String, val iconName: String) {
    DASHBOARD("Dashboard", "Dashboard"),
    CALENDAR("Calendar", "CalendarMonth"),
    FINANCE("Finance", "AttachMoney"),
    HEALTH("Health", "Favorite"),
    FOCUS("Focus", "Timer"),
    AI("AI", "AutoAwesome"),
    KNOWLEDGE("Knowledge", "MenuBook")
}

data class LifeOSPlugin(
    val type: PluginType,
    val isEnabled: Boolean = true,
    val version: String = "1.0.0",
    val description: String = ""
)

class PluginManager {
    private val plugins = mutableMapOf<PluginType, LifeOSPlugin>()

    init {
        PluginType.values().forEach { type ->
            plugins[type] = LifeOSPlugin(
                type = type,
                isEnabled = true,
                version = "1.0.0",
                description = "Módulo de ${type.displayName} ativo no LifeOS"
            )
        }
    }

    fun getAllPlugins(): List<LifeOSPlugin> = plugins.values.toList()

    fun getPlugin(type: PluginType): LifeOSPlugin? = plugins[type]

    fun togglePlugin(type: PluginType, enabled: Boolean) {
        plugins[type]?.let {
            plugins[type] = it.copy(isEnabled = enabled)
        }
    }
}

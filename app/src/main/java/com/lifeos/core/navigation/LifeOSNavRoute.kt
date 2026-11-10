package com.lifeos.core.navigation

sealed class LifeOSScreen(val route: String, val title: String, val iconName: String) {
    object Dashboard : LifeOSScreen("dashboard", "Dashboard", "dashboard")
    object Tasks : LifeOSScreen("tasks", "Tarefas", "check_circle")
    object Habits : LifeOSScreen("habits", "Hábitos", "repeat")
    object Projects : LifeOSScreen("projects", "Projetos", "folder")
    object Goals : LifeOSScreen("goals", "Metas", "flag")
    object Calendar : LifeOSScreen("calendar", "Calendário", "calendar_month")
    object Focus : LifeOSScreen("focus", "Foco", "timer")
    object Finance : LifeOSScreen("finance", "Finanças", "attach_money")
    object Health : LifeOSScreen("health", "Saúde", "favorite")
    object Knowledge : LifeOSScreen("knowledge", "Conhecimento", "menu_book")
    object Copilot : LifeOSScreen("copilot", "Copilot AI", "auto_awesome")
    object Settings : LifeOSScreen("settings", "Configurações", "settings")

    companion object {
        val bottomNavItems = listOf(Dashboard, Tasks, Habits, Focus, Copilot)
        val allScreens = listOf(
            Dashboard, Tasks, Habits, Projects, Goals,
            Calendar, Focus, Finance, Health, Knowledge,
            Copilot, Settings
        )
    }
}

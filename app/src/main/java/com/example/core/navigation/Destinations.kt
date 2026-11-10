package com.example.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destination(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val testTag: String
) {
    object Dashboard : Destination(Routes.DASHBOARD, "Dashboard AI", Icons.Default.Dashboard, "bottom_nav_dashboard")
    object Agenda : Destination(Routes.AGENDA, "Agenda", Icons.Default.FormatListBulleted, "bottom_nav_agenda")
    object Calendar : Destination(Routes.CALENDAR, "Analytics", Icons.Default.Leaderboard, "bottom_nav_analytics")
    object Tasks : Destination(Routes.TASKS, "Tarefas", Icons.Default.CheckCircle, "bottom_nav_tarefas")
    object Projects : Destination(Routes.PROJECTS, "Projetos", Icons.Default.Folder, "bottom_nav_projetos")
    object Habits : Destination(Routes.HABITS, "Hábitos", Icons.Default.Bolt, "bottom_nav_habitos")
    object Goals : Destination(Routes.GOALS, "Metas", Icons.Default.Star, "bottom_nav_metas")
    object Finances : Destination(Routes.FINANCES, "Finanças", Icons.Default.AccountBalanceWallet, "bottom_nav_financas")
    object Health : Destination(Routes.HEALTH, "Saúde", Icons.Default.Favorite, "bottom_nav_saude")
    object LifeAI : Destination(Routes.LIFE_AI, "LifeAI", Icons.Default.AutoAwesome, "bottom_nav_ia")
    object Profile : Destination(Routes.PROFILE, "Perfil", Icons.Default.Person, "bottom_nav_perfil")
    object Settings : Destination(Routes.SETTINGS, "Configurações", Icons.Default.Settings, "bottom_nav_config")

    companion object {
        val bottomBarDestinations = listOf(
            Dashboard,
            Tasks,
            Habits,
            Projects,
            LifeAI
        )

        val allDestinations = listOf(
            Dashboard,
            LifeAI,
            Habits,
            Tasks,
            Projects,
            Agenda,
            Goals,
            Profile,
            Settings
        )
    }
}

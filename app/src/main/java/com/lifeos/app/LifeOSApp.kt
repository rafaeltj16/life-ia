package com.lifeos.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.core.common.LifeOSViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lifeos.core.navigation.LifeOSScreen
import com.lifeos.features.calendar.LifeOSCalendarScreen
import com.lifeos.features.copilot.LifeOSCopilotScreen
import com.lifeos.features.dashboard.LifeOSDashboardScreen
import com.lifeos.features.finance.LifeOSFinanceScreen
import com.lifeos.features.focus.LifeOSFocusScreen
import com.lifeos.features.goals.LifeOSGoalsScreen
import com.lifeos.features.habits.presentation.screen.HabitsScreen
import com.lifeos.features.health.LifeOSHealthScreen
import com.lifeos.features.knowledge.LifeOSKnowledgeScreen
import com.lifeos.features.projects.LifeOSProjectsScreen
import com.lifeos.features.settings.LifeOSSettingsScreen
import com.lifeos.features.tasks.presentation.screen.TasksScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeOSAppRoot(
    viewModel: LifeOSViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<LifeOSScreen>(LifeOSScreen.Dashboard) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) },
                actions = {
                    IconButton(onClick = { currentScreen = LifeOSScreen.Copilot }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Copilot AI")
                    }
                    IconButton(onClick = { currentScreen = LifeOSScreen.Settings }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurações")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                LifeOSScreen.bottomNavItems.forEach { screen ->
                    val selected = currentScreen == screen
                    val icon: ImageVector = when (screen) {
                        LifeOSScreen.Dashboard -> Icons.Default.Dashboard
                        LifeOSScreen.Tasks -> Icons.Default.CheckCircle
                        LifeOSScreen.Habits -> Icons.Default.Repeat
                        LifeOSScreen.Focus -> Icons.Default.Timer
                        LifeOSScreen.Copilot -> Icons.Default.AutoAwesome
                        else -> Icons.Default.Circle
                    }

                    NavigationBarItem(
                        selected = selected,
                        onClick = { currentScreen = screen },
                        icon = { Icon(icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                LifeOSScreen.Dashboard -> LifeOSDashboardScreen(onNavigateTo = { route ->
                    when (route) {
                        "focus" -> currentScreen = LifeOSScreen.Focus
                        "tasks" -> currentScreen = LifeOSScreen.Tasks
                        "copilot" -> currentScreen = LifeOSScreen.Copilot
                        "projects" -> currentScreen = LifeOSScreen.Projects
                    }
                })
                LifeOSScreen.Tasks -> TasksScreen(viewModel)
                LifeOSScreen.Habits -> HabitsScreen(viewModel)
                LifeOSScreen.Projects -> LifeOSProjectsScreen()
                LifeOSScreen.Goals -> LifeOSGoalsScreen()
                LifeOSScreen.Calendar -> LifeOSCalendarScreen()
                LifeOSScreen.Focus -> LifeOSFocusScreen()
                LifeOSScreen.Finance -> LifeOSFinanceScreen()
                LifeOSScreen.Health -> LifeOSHealthScreen()
                LifeOSScreen.Knowledge -> LifeOSKnowledgeScreen()
                LifeOSScreen.Copilot -> LifeOSCopilotScreen()
                LifeOSScreen.Settings -> LifeOSSettingsScreen()
            }
        }
    }
}

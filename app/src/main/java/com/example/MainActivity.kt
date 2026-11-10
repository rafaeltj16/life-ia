package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import kotlinx.coroutines.launch
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.common.AuthState
import com.example.core.common.NavigationModule
import com.example.core.ui.LifeOSAvatar
import com.example.core.ui.LifeOSInput
import com.example.core.ui.LifeOSToast
import com.example.core.design.MyApplicationTheme
import com.example.features.auth.presentation.screen.AuthScreen
import com.example.features.calendar.presentation.screen.AgendaScreen
import com.example.features.calendar.presentation.screen.CalendarScreen
import com.lifeos.features.tasks.presentation.screen.TasksScreen
import com.example.features.projects.presentation.screen.ProjectsScreen
import com.lifeos.features.habits.presentation.screen.HabitsScreen
import com.lifeos.features.habits.presentation.screen.GoalsScreen
import com.example.features.finance.presentation.screen.FinancesScreen
import com.example.features.health.presentation.screen.HealthWorkoutScreen
import com.example.features.ai.presentation.screen.LifeAIScreen
import com.example.features.settings.presentation.screen.SettingsScreen
import com.example.core.common.LifeOSViewModel
import com.example.core.firebase.configureAppCheck
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.Routes

class MainActivity : ComponentActivity() {
    private val viewModel: LifeOSViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure App Check before UI or Firebase operations
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            
            MyApplicationTheme(darkTheme = isDarkMode) {
                val authState by viewModel.authState.collectAsState()
                val activeToast by viewModel.toast.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (authState) {
                        is AuthState.Unauthenticated -> {
                            AuthScreen(viewModel = viewModel)
                        }
                        is AuthState.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Sincronizando LifeOS AI...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                        is AuthState.Authenticated -> {
                            MainAppContent(viewModel = viewModel)
                        }
                    }

                    // Global floating structured toast notifications
                    activeToast?.let { toastInfo ->
                        LifeOSToast(
                            message = toastInfo.message,
                            type = toastInfo.type,
                            onDismiss = { viewModel.clearToast() }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: LifeOSViewModel) {
    val currentModule by viewModel.currentModule.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val navController = rememberNavController()

    // Sync currentModule StateFlow -> NavController
    LaunchedEffect(currentModule) {
        val route = when (currentModule) {
            NavigationModule.DASHBOARD -> Routes.DASHBOARD
            NavigationModule.LIFE_AI -> Routes.LIFE_AI
            NavigationModule.HABITS -> Routes.HABITS
            NavigationModule.TASKS -> Routes.TASKS
            NavigationModule.PROJECTS -> Routes.PROJECTS
            NavigationModule.AGENDA -> Routes.AGENDA
            NavigationModule.GOALS -> Routes.GOALS
            NavigationModule.FINANCES -> Routes.FINANCES
            NavigationModule.HEALTH -> Routes.HEALTH
            NavigationModule.PROFILE -> Routes.PROFILE
            NavigationModule.SETTINGS -> Routes.SETTINGS
        }
        if (navController.currentBackStackEntry?.destination?.route != route) {
            navController.navigate(route) {
                popUpTo(Routes.DASHBOARD) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // Sync NavController -> currentModule StateFlow
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val module = when (destination.route) {
                Routes.DASHBOARD -> NavigationModule.DASHBOARD
                Routes.LIFE_AI -> NavigationModule.LIFE_AI
                Routes.HABITS -> NavigationModule.HABITS
                Routes.TASKS -> NavigationModule.TASKS
                Routes.PROJECTS -> NavigationModule.PROJECTS
                Routes.AGENDA -> NavigationModule.AGENDA
                Routes.GOALS -> NavigationModule.GOALS
                Routes.FINANCES -> NavigationModule.FINANCES
                Routes.HEALTH -> NavigationModule.HEALTH
                Routes.PROFILE -> NavigationModule.PROFILE
                Routes.SETTINGS -> NavigationModule.SETTINGS
                else -> null
            }
            if (module != null && currentModule != module) {
                viewModel.setModule(module)
            }
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    val userName = when (val state = authState) {
        is AuthState.Authenticated -> state.name
        else -> "Usuário"
    }

    val userEmail = when (val state = authState) {
        is AuthState.Authenticated -> state.email
        else -> "user@lifeos.ai"
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Handle system back gesture
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    BackHandler(enabled = !drawerState.isOpen && currentModule != NavigationModule.DASHBOARD) {
        viewModel.setModule(NavigationModule.DASHBOARD)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight(),
                drawerContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
            ) {
                // Header of Drawer with Branding
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.tertiary
                                        )
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Adjust, contentDescription = "Logo LifeOS AI", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "LifeOS AI",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // User Info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        LifeOSAvatar(name = userName, size = 38.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(userName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                            Text(userEmail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                // Navigation list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(NavigationModule.values()) { module ->
                        val isSelected = currentModule == module
                        val icon = getModuleIcon(module)

                        NavigationDrawerItem(
                            label = { Text(module.title, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                            selected = isSelected,
                            icon = { Icon(icon, contentDescription = "Ir para o módulo ${module.title}", tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant) },
                            onClick = {
                                viewModel.setModule(module)
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                unselectedContainerColor = Color.Transparent,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("drawer_item_${module.name.lowercase()}")
                        )
                    }
                }

                // Bottom actions
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Modo Escuro",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("drawer_dark_theme_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.Brightness3,
                            contentDescription = "Alternar para tema claro ou escuro",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                // Fixed top bar / navigation bar
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "LifeOS AI",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Módulo / ${currentModule.title}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.testTag("navigation_menu_button")
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Abrir menu lateral")
                        }
                    },
                    actions = {
                        // Quick AI Copilot shortcut
                        IconButton(
                            onClick = { viewModel.setModule(NavigationModule.LIFE_AI) },
                            modifier = Modifier.testTag("top_app_bar_copilot_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Abrir Copiloto LifeAI",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Quick theme toggle
                        IconButton(
                            onClick = { viewModel.toggleTheme() },
                            modifier = Modifier.testTag("top_app_bar_dark_theme_toggle")
                        ) {
                            Icon(
                                imageVector = if (isDarkMode) Icons.Default.WbSunny else Icons.Default.Brightness3,
                                contentDescription = "Alternar cores claras e escuras",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // User avatar menu trigger
                        LifeOSAvatar(
                            name = userName,
                            modifier = Modifier
                                .padding(end = 12.dp, start = 4.dp)
                                .testTag("top_app_bar_avatar"),
                            onClick = { viewModel.showToast("Dica: Clique em 'Encerrar Sessão' no final das Configurações para deslogar!", com.example.core.common.ToastType.INFO) }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    modifier = Modifier.border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                )
            },
            bottomBar = {
                com.example.core.navigation.BottomNavigation(
                    navController = navController,
                    modifier = Modifier.border(
                        width = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            com.example.core.navigation.AppGraph(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

fun getModuleIcon(module: NavigationModule): ImageVector {
    return when (module) {
        NavigationModule.DASHBOARD -> Icons.Default.Dashboard
        NavigationModule.LIFE_AI -> Icons.Default.AutoAwesome
        NavigationModule.HABITS -> Icons.Default.Bolt
        NavigationModule.TASKS -> Icons.Default.CheckCircle
        NavigationModule.PROJECTS -> Icons.Default.Folder
        NavigationModule.AGENDA -> Icons.Default.FormatListBulleted
        NavigationModule.GOALS -> Icons.Default.Star
        NavigationModule.FINANCES -> Icons.Default.AccountBalanceWallet
        NavigationModule.HEALTH -> Icons.Default.Favorite
        NavigationModule.PROFILE -> Icons.Default.Person
        NavigationModule.SETTINGS -> Icons.Default.Settings
    }
}

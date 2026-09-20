package com.resq.features.home.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.resq.features.home.presentation.ui.HomeScreen
import com.resq.features.home.presentation.navigation.NavDestination
import com.resq.features.home.presentation.ui.components.ResQNavigationBar

import com.resq.features.contacts.presentation.ui.ContactsScreen
import com.resq.features.deadman.presentation.ui.DeadManScreen
import com.resq.features.settings.presentation.ui.SettingsScreen
import com.resq.features.presets.presentation.ui.PresetsScreen




@Composable
fun HomeNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Resolve current NavDestination for bottom bar highlighting
    val currentDestination = NavDestination.values().firstOrNull { it.route == currentRoute }
        ?: NavDestination.HOME

    Scaffold(
        bottomBar = {
            // Only show navigation bar on main top-level tab routes
            val showBottomBar = NavDestination.values().any { it.route == currentRoute }
            if (showBottomBar) {
                ResQNavigationBar(
                    currentDestination = currentDestination,
                    onDestinationSelected = { selectedDestination ->
                        navController.navigate(selectedDestination.route) {
                            // Pop up to the start destination to avoid building up a large back stack
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when reselecting
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.HOME.route,
            modifier = modifier.padding(innerPadding)
        ) {
            // 1. Home Screen Tab
            composable(NavDestination.HOME.route) {
                HomeScreen(navController = navController)
            }

            // 2. Presets List Tab
            composable(NavDestination.PRESETS.route) {
                PresetsScreen(navController = navController)
            }

            // 3. Dead Man's Switch Tab
            composable(NavDestination.DEAD_MAN.route) {
                DeadManScreen(navController = navController)
            }

            // 4. Contacts Tab
            composable(NavDestination.CONTACTS.route) {
                ContactsScreen(navController = navController)
            }

            // 5. Settings & Duress PIN Tab
            composable(NavDestination.SETTINGS.route) {
                SettingsScreen(navController = navController)
            }

            /* --- Secondary / Sub-Screens (Bottom bar hidden automatically) --- */
            
            // Example: Preset Editor
            composable("preset_edit/{presetId}") { backStackEntry ->
                val presetId = backStackEntry.arguments?.getString("presetId")
                // PresetEditScreen(navController = navController, presetId = presetId)
            }
        }
    }
}

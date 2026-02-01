package com.wakey.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.wakey.app.ui.alarmedit.AlarmEditScreen
import com.wakey.app.ui.alarmlist.AlarmListScreen
import com.wakey.app.ui.settings.SettingsScreen

/**
 * Main navigation graph for the app.
 * Defines all navigable destinations and their transitions.
 */
@Composable
fun AppNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.AlarmList.route
    ) {
        // ============================================
        // ALARM LIST SCREEN
        // ============================================
        composable(route = Screen.AlarmList.route) {
            AlarmListScreen(
                onAddAlarm = {
                    navController.navigate(Screen.AlarmEdit.createRoute(-1))
                },
                onEditAlarm = { alarmId ->
                    navController.navigate(Screen.AlarmEdit.createRoute(alarmId))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }
        
        // ============================================
        // ALARM EDIT SCREEN
        // ============================================
        composable(
            route = Screen.AlarmEdit.route,
            arguments = listOf(
                navArgument("alarmId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) {
            AlarmEditScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // ============================================
        // SETTINGS SCREEN
        // ============================================
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        // Note: AlarmRingingActivity is a separate Activity, not part of NavGraph
    }
}

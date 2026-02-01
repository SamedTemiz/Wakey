package com.wakey.app.ui.navigation

/**
 * Navigation routes for the app.
 * Defines all screens and their navigation parameters.
 */
sealed class Screen(val route: String) {
    
    /**
     * Main alarm list screen.
     * Shows all configured alarms with toggle switches.
     */
    data object AlarmList : Screen("alarm_list")
    
    /**
     * Alarm edit screen.
     * Used for creating new alarms or editing existing ones.
     * 
     * @param alarmId Alarm ID for editing, -1 for new alarm
     */
    data object AlarmEdit : Screen("alarm_edit/{alarmId}") {
        fun createRoute(alarmId: Int = -1) = "alarm_edit/$alarmId"
    }
    
    /**
     * Alarm ring screen (full screen when alarm triggers).
     * Shows the task UI and blocks dismissal until task is complete.
     */
    data object AlarmRing : Screen("alarm_ring/{alarmId}") {
        fun createRoute(alarmId: Int) = "alarm_ring/$alarmId"
    }
    
    /**
     * Settings screen.
     * Allows users to configure default sound, task type, vibration, etc.
     */
    data object Settings : Screen("settings")
}

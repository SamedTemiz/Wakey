package com.wakey.app.ui.settings

import com.wakey.app.data.model.TaskType

/**
 * UI State for Settings Screen.
 * Contains all settings values and loading state.
 */
data class SettingsUiState(
    // Loading state
    val isLoading: Boolean = false,
    
    // Sound settings
    val defaultSoundUri: String = "",
    val defaultSoundName: String = "Default",
    
    // Task settings
    val defaultTaskType: TaskType = TaskType.STEPS,
    
    // Vibration
    val vibrationEnabled: Boolean = true,
    
    // Snooze settings
    val snoozeDurationMinutes: Int = 5,
    val maxSnoozeCount: Int = 3,
    
    // Task-specific settings
    val defaultStepCount: Int = 30,
    val defaultDelaySeconds: Int = 15,
    val defaultHoldSeconds: Int = 20,
    
    // App info
    val appVersion: String = "1.0.0",
    
    // Error state
    val errorMessage: String? = null
)

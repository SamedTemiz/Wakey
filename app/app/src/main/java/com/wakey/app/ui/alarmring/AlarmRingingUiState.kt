package com.wakey.app.ui.alarmring

import com.wakey.app.data.model.Alarm

/**
 * UI state for the Alarm Ringing screen.
 */
data class AlarmRingingUiState(
    val alarm: Alarm? = null,
    val isLoading: Boolean = true,
    val taskProgress: Float = 0f,
    val isTaskComplete: Boolean = false,
    val timeRemaining: Int = 0, // For time-based tasks (seconds)
    val stepCount: Int = 0, // For step counter task
    val isDeviceVertical: Boolean = false, // For vertical hold task
    val errorMessage: String? = null
)

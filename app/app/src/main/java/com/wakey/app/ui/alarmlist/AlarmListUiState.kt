package com.wakey.app.ui.alarmlist

import com.wakey.app.data.model.Alarm

/**
 * UI state for the Alarm List screen
 */
data class AlarmListUiState(
    val alarms: List<Alarm> = emptyList(),
    val isLoading: Boolean = true,
    val canAddAlarm: Boolean = true,
    val errorMessage: String? = null,
    val toastMessage: String? = null
)

package com.wakey.app.ui.alarmedit

import com.wakey.app.data.model.TaskType

/**
 * UI state for the Alarm Edit screen
 */
data class AlarmEditUiState(
    val alarmId: Int? = null,
    val hour: Int = 7,
    val minute: Int = 0,
    val isEnabled: Boolean = true,
    val selectedDays: Set<Int> = emptySet(), // 0=Sun, 1=Mon, ..., 6=Sat
    val taskType: TaskType = TaskType.TIME_DELAY,
    val ringtoneUri: String = "",
    val label: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val toastMessage: String? = null
) {
    val isEditMode: Boolean get() = alarmId != null
}

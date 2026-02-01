package com.wakey.app.ui.alarmedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakey.app.data.model.Alarm
import com.wakey.app.data.model.TaskType
import com.wakey.app.data.repository.AlarmRepository
import com.wakey.app.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Alarm Edit screen.
 * Handles creating new alarms and editing existing ones.
 */
@HiltViewModel
class AlarmEditViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: com.wakey.app.data.repository.SettingsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val alarmId: Int? = savedStateHandle.get<Int>("alarmId")?.takeIf { it != -1 }
    
    private val _uiState = MutableStateFlow(AlarmEditUiState())
    val uiState: StateFlow<AlarmEditUiState> = _uiState.asStateFlow()

    init {
        if (alarmId != null) {
            loadAlarm(alarmId)
        }
    }
    
    /**
     * Load existing alarm for editing
     */
    private fun loadAlarm(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val alarm = alarmRepository.getAlarmById(id)
            if (alarm != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        alarmId = alarm.id,
                        hour = alarm.hour,
                        minute = alarm.minute,
                        isEnabled = alarm.isEnabled,
                        selectedDays = alarm.getRepeatDaysList().toSet(),
                        taskType = alarm.taskType,
                        ringtoneUri = alarm.ringtoneUri,
                        label = alarm.label,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Alarm not found") }
            }
        }
    }
    
    /**
     * Update the time
     */
    fun setTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(hour = hour, minute = minute) }
    }
    
    /**
     * Toggle a repeat day
     */
    fun toggleDay(day: Int) {
        _uiState.update { currentState ->
            val newDays = currentState.selectedDays.toMutableSet()
            if (newDays.contains(day)) {
                newDays.remove(day)
            } else {
                newDays.add(day)
            }
            currentState.copy(selectedDays = newDays)
        }
    }
    
    /**
     * Set the task type
     */
    fun setTaskType(taskType: TaskType) {
        _uiState.update { it.copy(taskType = taskType) }
    }
    
    /**
     * Set the label
     */
    fun setLabel(label: String) {
        _uiState.update { it.copy(label = label) }
    }
    
    /**
     * Set the ringtone URI
     */
    fun setRingtoneUri(uri: String) {
        _uiState.update { it.copy(ringtoneUri = uri) }
    }
    
    /**
     * Save the alarm
     */
    fun saveAlarm() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            val state = _uiState.value
            val repeatDays = state.selectedDays.sorted().joinToString(",")
            
            val alarm = Alarm(
                id = state.alarmId ?: 0,
                hour = state.hour,
                minute = state.minute,
                isEnabled = state.isEnabled,
                repeatDays = repeatDays,
                taskType = state.taskType,
                ringtoneUri = state.ringtoneUri,
                label = state.label
            )
            
            try {
                if (state.isEditMode) {
                    alarmRepository.updateAlarm(alarm)
                } else {
                    val result = alarmRepository.insertAlarm(alarm)
                    if (result == -1L) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessage = "Maximum 3 alarms allowed"
                            ) 
                        }
                        return@launch
                    }
                }
                
                // Schedule the alarm with AlarmManager
                alarmScheduler.scheduleAlarm(alarm)
                
                // Generate toast message with time until alarm
                val toastMessage = com.wakey.app.utils.TimeFormatter.getAlarmSetMessage(alarm)
                
                _uiState.update { it.copy(isLoading = false, isSaved = true, toastMessage = toastMessage) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, errorMessage = e.message ?: "Error saving alarm") 
                }
            }
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Reset alarm settings to defaults
     */
    fun resetToDefaults() {
        val defaultHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val defaultMinute = 0
        
        _uiState.update { currentState ->
            currentState.copy(
                hour = defaultHour,
                minute = defaultMinute,
                selectedDays = emptySet(),
                taskType = TaskType.STEPS,
                label = ""
            )
        }
    }
}

package com.wakey.app.ui.alarmlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakey.app.data.model.Alarm
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
 * ViewModel for the Alarm List screen.
 * Manages the UI state and handles user interactions.
 */
@HiltViewModel
class AlarmListViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    private val settingsRepository: com.wakey.app.data.repository.SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AlarmListUiState())
    val uiState: StateFlow<AlarmListUiState> = _uiState.asStateFlow()
    
    init {
        loadAlarms()
    }
    
    /**
     * Load all alarms from repository
     */
    private fun loadAlarms() {
        viewModelScope.launch {
            alarmRepository.getAllAlarms().collect { alarms ->
                val canAdd = alarmRepository.canAddAlarm()
                _uiState.update { currentState ->
                    currentState.copy(
                        alarms = alarms,
                        isLoading = false,
                        canAddAlarm = canAdd
                    )
                }
            }
        }
    }
    
    /**
     * Toggle alarm enabled state
     */
    fun toggleAlarm(alarm: Alarm) {
        viewModelScope.launch {
            val newState = !alarm.isEnabled
            alarmRepository.setAlarmEnabled(alarm.id, newState)
            
            // Schedule or cancel alarm based on new state
            if (newState) {
                // Alarm was enabled, schedule it
                val updatedAlarm = alarm.copy(isEnabled = true)
                alarmScheduler.scheduleAlarm(updatedAlarm)
                
                // Show toast with time until alarm
                val message = com.wakey.app.utils.TimeFormatter.getAlarmSetMessage(updatedAlarm)
                _uiState.update { it.copy(toastMessage = message) }
            } else {
                // Alarm was disabled, cancel it
                alarmScheduler.cancelAlarm(alarm.id)
                _uiState.update { it.copy(toastMessage = "Alarm disabled") }
            }
        }
    }
    
    /**
     * Delete an alarm
     */
    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch {
            // Cancel scheduled alarm first
            alarmScheduler.cancelAlarm(alarm.id)
            // Then delete from database
            alarmRepository.deleteAlarm(alarm)
        }
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Clear toast message
     */
    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
    
    /**
     * Check if new alarm can be added
     */
    fun checkCanAddAlarm(): Boolean {
        return _uiState.value.canAddAlarm
    }
}

package com.wakey.app.ui.alarmring

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakey.app.data.model.Alarm
import com.wakey.app.data.model.TaskType
import com.wakey.app.data.repository.AlarmRepository
import com.wakey.app.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Alarm Ringing screen.
 * Manages alarm state and task progress.
 */
@HiltViewModel
class AlarmRingingViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private var alarmId: Int = -1
    
    private val _uiState = MutableStateFlow(AlarmRingingUiState())
    val uiState: StateFlow<AlarmRingingUiState> = _uiState.asStateFlow()
    
    /**
     * Initialize with alarm ID (called from activity/composable)
     */
    fun init(id: Int) {
        // Only load if not already initialized or if ID changed
        if (alarmId != id) {
            alarmId = id
            loadAlarm()
        }
    }
    
    /**
     * Load the alarm details
     */
    private fun loadAlarm() {
        viewModelScope.launch {
            val alarm = alarmRepository.getAlarmById(alarmId)
            if (alarm != null) {
                _uiState.update {
                    it.copy(
                        alarm = alarm,
                        isLoading = false,
                        timeRemaining = getTaskDuration(alarm.taskType)
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Alarm not found"
                    )
                }
            }
        }
    }
    
    /**
     * Get task duration in seconds for time-based tasks
     */
    private fun getTaskDuration(taskType: TaskType): Int {
        return when (taskType) {
            TaskType.HOLD_VERTICAL -> 20
            TaskType.TIME_DELAY -> 15
            else -> 0
        }
    }
    
    /**
     * Update step count for step counter task
     */
    fun updateStepCount(steps: Int) {
        _uiState.update {
            val progress = (steps.toFloat() / 30f).coerceIn(0f, 1f)
            it.copy(
                stepCount = steps,
                taskProgress = progress,
                isTaskComplete = steps >= 30
            )
        }
    }
    
    /**
     * Update vertical hold status
     */
    fun updateVerticalHold(isVertical: Boolean, elapsedSeconds: Int) {
        _uiState.update {
            val remaining = 20 - elapsedSeconds
            val progress = (elapsedSeconds.toFloat() / 20f).coerceIn(0f, 1f)
            it.copy(
                isDeviceVertical = isVertical,
                timeRemaining = remaining.coerceAtLeast(0),
                taskProgress = progress,
                isTaskComplete = elapsedSeconds >= 20
            )
        }
    }
    
    /**
     * Update delay task timer
     */
    fun updateDelayTimer(elapsedSeconds: Int) {
        _uiState.update {
            val remaining = 15 - elapsedSeconds
            val progress = (elapsedSeconds.toFloat() / 15f).coerceIn(0f, 1f)
            it.copy(
                timeRemaining = remaining.coerceAtLeast(0),
                taskProgress = progress,
                isTaskComplete = elapsedSeconds >= 15
            )
        }
    }
    
    /**
     * Snooze the alarm (5 minutes)
     */
    fun snoozeAlarm() {
        viewModelScope.launch {
            val alarm = _uiState.value.alarm ?: return@launch
            
            // Create a temporary alarm for snooze (5 minutes from now)
            val now = System.currentTimeMillis()
            val snoozeTime = now + (5 * 60 * 1000) // 5 minutes
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = snoozeTime
            }
            
            val snoozeAlarm = alarm.copy(
                hour = calendar.get(java.util.Calendar.HOUR_OF_DAY),
                minute = calendar.get(java.util.Calendar.MINUTE),
                repeatDays = "" // One-time alarm for snooze
            )
            
            alarmScheduler.scheduleAlarm(snoozeAlarm)
        }
    }
    
    /**
     * Reschedule alarm if it's a repeating alarm
     */
    fun rescheduleIfNeeded() {
        viewModelScope.launch {
            val alarm = _uiState.value.alarm ?: return@launch
            
            if (alarm.getRepeatDaysList().isNotEmpty()) {
                // If repeating, just schedule the next occurrence
                alarmScheduler.scheduleAlarm(alarm)
            } else {
                // If one-time, disable it in database so it doesn't reschedule on boot
                alarmRepository.setAlarmEnabled(alarm.id, false)
            }
        }
    }
}

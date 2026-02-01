package com.wakey.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wakey.app.data.model.TaskType
import com.wakey.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Settings Screen.
 * Manages settings state and handles user interactions.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    /**
     * Load all settings from repository
     */
    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Collect all settings from repository flows
            launch {
                settingsRepository.defaultSoundUri.collect { uri ->
                    _uiState.update { it.copy(defaultSoundUri = uri) }
                }
            }
            
            launch {
                settingsRepository.defaultSoundName.collect { name ->
                    _uiState.update { it.copy(defaultSoundName = name) }
                }
            }
            
            launch {
                settingsRepository.defaultTaskType.collect { taskType ->
                    _uiState.update { it.copy(defaultTaskType = taskType) }
                }
            }
            
            launch {
                settingsRepository.vibrationEnabled.collect { enabled ->
                    _uiState.update { it.copy(vibrationEnabled = enabled) }
                }
            }
            
            launch {
                settingsRepository.snoozeDurationMinutes.collect { minutes ->
                    _uiState.update { it.copy(snoozeDurationMinutes = minutes) }
                }
            }
            
            launch {
                settingsRepository.maxSnoozeCount.collect { count ->
                    _uiState.update { it.copy(maxSnoozeCount = count) }
                }
            }
            
            launch {
                settingsRepository.defaultStepCount.collect { steps ->
                    _uiState.update { it.copy(defaultStepCount = steps) }
                }
            }
            
            launch {
                settingsRepository.defaultDelaySeconds.collect { seconds ->
                    _uiState.update { it.copy(defaultDelaySeconds = seconds) }
                }
            }
            
            launch {
                settingsRepository.defaultHoldSeconds.collect { seconds ->
                    _uiState.update { it.copy(defaultHoldSeconds = seconds) }
                }
            }
            
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    appVersion = SettingsRepository.APP_VERSION
                ) 
            }
        }
    }
    
    // ============================================
    // SOUND SETTINGS
    // ============================================
    
    /**
     * Set default alarm sound
     */
    fun setDefaultSound(uri: String, name: String) {
        settingsRepository.setDefaultSound(uri, name)
    }
    
    // ============================================
    // TASK SETTINGS
    // ============================================
    
    /**
     * Set default task type for new alarms
     */
    fun setDefaultTaskType(taskType: TaskType) {
        settingsRepository.setDefaultTaskType(taskType)
    }
    
    // ============================================
    // VIBRATION SETTINGS
    // ============================================
    
    /**
     * Toggle vibration on/off
     */
    fun toggleVibration() {
        val currentValue = _uiState.value.vibrationEnabled
        settingsRepository.setVibrationEnabled(!currentValue)
    }
    
    /**
     * Set vibration enabled state
     */
    fun setVibrationEnabled(enabled: Boolean) {
        settingsRepository.setVibrationEnabled(enabled)
    }
    
    // ============================================
    // SNOOZE SETTINGS
    // ============================================
    
    /**
     * Set snooze duration in minutes
     */
    fun setSnoozeDuration(minutes: Int) {
        settingsRepository.setSnoozeDuration(minutes)
    }
    
    /**
     * Set maximum snooze count
     */
    fun setMaxSnoozeCount(count: Int) {
        settingsRepository.setMaxSnoozeCount(count)
    }
    
    // ============================================
    // TASK-SPECIFIC SETTINGS
    // ============================================
    
    /**
     * Set default step count for step task
     */
    fun setDefaultStepCount(steps: Int) {
        settingsRepository.setDefaultStepCount(steps)
    }
    
    /**
     * Set default delay seconds for delay task
     */
    fun setDefaultDelaySeconds(seconds: Int) {
        settingsRepository.setDefaultDelaySeconds(seconds)
    }
    
    /**
     * Set default hold seconds for hold vertical task
     */
    fun setDefaultHoldSeconds(seconds: Int) {
        settingsRepository.setDefaultHoldSeconds(seconds)
    }
    
    // ============================================
    // UTILITY FUNCTIONS
    // ============================================
    
    /**
     * Reset all settings to defaults
     */
    fun resetToDefaults() {
        settingsRepository.resetToDefaults()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
    
    /**
     * Get privacy policy URL
     */
    fun getPrivacyPolicyUrl(): String {
        return SettingsRepository.PRIVACY_POLICY_URL
    }
}

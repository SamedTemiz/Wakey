package com.wakey.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.RingtoneManager
import com.wakey.app.data.model.TaskType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing app-wide settings.
 * Stores preferences in SharedPreferences and exposes them as StateFlows.
 */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
    
    // ============================================
    // PREFERENCE KEYS
    // ============================================
    
    companion object {
        // Theme settings (kept for future use, currently enforced light)
        private const val KEY_IS_DARK_THEME = "is_dark_theme"
        
        // Sound settings
        private const val KEY_DEFAULT_SOUND_URI = "default_sound_uri"
        private const val KEY_DEFAULT_SOUND_NAME = "default_sound_name"
        
        // Task settings
        private const val KEY_DEFAULT_TASK_TYPE = "default_task_type"
        
        // Alarm behavior settings
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_SNOOZE_DURATION_MINUTES = "snooze_duration_minutes"
        private const val KEY_MAX_SNOOZE_COUNT = "max_snooze_count"
        
        // Step task settings
        private const val KEY_DEFAULT_STEP_COUNT = "default_step_count"
        
        // Delay task settings
        private const val KEY_DEFAULT_DELAY_SECONDS = "default_delay_seconds"
        
        // Hold vertical task settings
        private const val KEY_DEFAULT_HOLD_SECONDS = "default_hold_seconds"
        
        // App info
        const val APP_VERSION = "1.0.0"
        const val PRIVACY_POLICY_URL = "https://github.com/user/wakey/blob/main/PRIVACY_POLICY.md"
        
        // Default values
        private const val DEFAULT_SNOOZE_DURATION = 5
        private const val DEFAULT_MAX_SNOOZE_COUNT = 3
        private const val DEFAULT_STEP_COUNT = 30
        private const val DEFAULT_DELAY_SECONDS = 15
        private const val DEFAULT_HOLD_SECONDS = 20
    }
    
    // ============================================
    // STATE FLOWS
    // ============================================
    
    // Theme (kept for compatibility, but enforced light mode)
    private val _isDarkTheme = MutableStateFlow(false) // Always light mode
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    // Default sound
    private val _defaultSoundUri = MutableStateFlow(getDefaultSoundUri())
    val defaultSoundUri: StateFlow<String> = _defaultSoundUri.asStateFlow()
    
    private val _defaultSoundName = MutableStateFlow(getDefaultSoundName())
    val defaultSoundName: StateFlow<String> = _defaultSoundName.asStateFlow()
    
    // Default task type
    private val _defaultTaskType = MutableStateFlow(getInitialDefaultTaskType())
    val defaultTaskType: StateFlow<TaskType> = _defaultTaskType.asStateFlow()
    
    // Vibration
    private val _vibrationEnabled = MutableStateFlow(getInitialVibrationEnabled())
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()
    
    // Snooze settings
    private val _snoozeDurationMinutes = MutableStateFlow(getInitialSnoozeDuration())
    val snoozeDurationMinutes: StateFlow<Int> = _snoozeDurationMinutes.asStateFlow()
    
    private val _maxSnoozeCount = MutableStateFlow(getInitialMaxSnoozeCount())
    val maxSnoozeCount: StateFlow<Int> = _maxSnoozeCount.asStateFlow()
    
    // Task-specific settings
    private val _defaultStepCount = MutableStateFlow(getInitialStepCount())
    val defaultStepCount: StateFlow<Int> = _defaultStepCount.asStateFlow()
    
    private val _defaultDelaySeconds = MutableStateFlow(getInitialDelaySeconds())
    val defaultDelaySeconds: StateFlow<Int> = _defaultDelaySeconds.asStateFlow()
    
    private val _defaultHoldSeconds = MutableStateFlow(getInitialHoldSeconds())
    val defaultHoldSeconds: StateFlow<Int> = _defaultHoldSeconds.asStateFlow()
    
    // ============================================
    // THEME SETTINGS (kept for compatibility)
    // ============================================
    
    /**
     * Toggle theme (no-op since we enforce light mode)
     */
    fun toggleTheme() {
        // No-op - light mode is enforced
        // Kept for API compatibility
    }
    
    /**
     * Check if system is currently dark mode
     */
    private fun isSystemDarkTheme(): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
    
    // ============================================
    // SOUND SETTINGS
    // ============================================
    
    /**
     * Set the default alarm sound
     */
    fun setDefaultSound(uri: String, name: String) {
        prefs.edit()
            .putString(KEY_DEFAULT_SOUND_URI, uri)
            .putString(KEY_DEFAULT_SOUND_NAME, name)
            .apply()
        _defaultSoundUri.value = uri
        _defaultSoundName.value = name
    }
    
    /**
     * Get the default sound URI
     */
    private fun getDefaultSoundUri(): String {
        val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)?.toString() ?: ""
        return prefs.getString(KEY_DEFAULT_SOUND_URI, defaultUri) ?: defaultUri
    }
    
    /**
     * Get the default sound name
     */
    private fun getDefaultSoundName(): String {
        val storedName = prefs.getString(KEY_DEFAULT_SOUND_NAME, null)
        if (storedName != null) return storedName
        
        // Try to get name from default URI
        return try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context, uri)
            ringtone?.getTitle(context) ?: "Default"
        } catch (e: Exception) {
            "Default"
        }
    }
    
    // ============================================
    // TASK SETTINGS
    // ============================================
    
    /**
     * Set the default task type for new alarms
     */
    fun setDefaultTaskType(taskType: TaskType) {
        prefs.edit().putString(KEY_DEFAULT_TASK_TYPE, taskType.name).apply()
        _defaultTaskType.value = taskType
    }
    
    /**
     * Get the initial default task type
     */
    private fun getInitialDefaultTaskType(): TaskType {
        val stored = prefs.getString(KEY_DEFAULT_TASK_TYPE, null)
        return if (stored != null) {
            try {
                TaskType.valueOf(stored)
            } catch (e: IllegalArgumentException) {
                TaskType.STEPS
            }
        } else {
            TaskType.STEPS
        }
    }
    
    // ============================================
    // VIBRATION SETTINGS
    // ============================================
    
    /**
     * Enable or disable vibration for alarms
     */
    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
        _vibrationEnabled.value = enabled
    }
    
    /**
     * Get initial vibration setting
     */
    private fun getInitialVibrationEnabled(): Boolean {
        return prefs.getBoolean(KEY_VIBRATION_ENABLED, true) // Default: enabled
    }
    
    // ============================================
    // SNOOZE SETTINGS
    // ============================================
    
    /**
     * Set snooze duration in minutes
     */
    fun setSnoozeDuration(minutes: Int) {
        val clampedValue = minutes.coerceIn(1, 30)
        prefs.edit().putInt(KEY_SNOOZE_DURATION_MINUTES, clampedValue).apply()
        _snoozeDurationMinutes.value = clampedValue
    }
    
    /**
     * Get initial snooze duration
     */
    private fun getInitialSnoozeDuration(): Int {
        return prefs.getInt(KEY_SNOOZE_DURATION_MINUTES, DEFAULT_SNOOZE_DURATION)
    }
    
    /**
     * Set maximum snooze count
     */
    fun setMaxSnoozeCount(count: Int) {
        val clampedValue = count.coerceIn(1, 10)
        prefs.edit().putInt(KEY_MAX_SNOOZE_COUNT, clampedValue).apply()
        _maxSnoozeCount.value = clampedValue
    }
    
    /**
     * Get initial max snooze count
     */
    private fun getInitialMaxSnoozeCount(): Int {
        return prefs.getInt(KEY_MAX_SNOOZE_COUNT, DEFAULT_MAX_SNOOZE_COUNT)
    }
    
    // ============================================
    // TASK-SPECIFIC SETTINGS
    // ============================================
    
    /**
     * Set default step count for step task
     */
    fun setDefaultStepCount(steps: Int) {
        val clampedValue = steps.coerceIn(10, 100)
        prefs.edit().putInt(KEY_DEFAULT_STEP_COUNT, clampedValue).apply()
        _defaultStepCount.value = clampedValue
    }
    
    private fun getInitialStepCount(): Int {
        return prefs.getInt(KEY_DEFAULT_STEP_COUNT, DEFAULT_STEP_COUNT)
    }
    
    /**
     * Set default delay seconds for delay task
     */
    fun setDefaultDelaySeconds(seconds: Int) {
        val clampedValue = seconds.coerceIn(5, 60)
        prefs.edit().putInt(KEY_DEFAULT_DELAY_SECONDS, clampedValue).apply()
        _defaultDelaySeconds.value = clampedValue
    }
    
    private fun getInitialDelaySeconds(): Int {
        return prefs.getInt(KEY_DEFAULT_DELAY_SECONDS, DEFAULT_DELAY_SECONDS)
    }
    
    /**
     * Set default hold seconds for hold vertical task
     */
    fun setDefaultHoldSeconds(seconds: Int) {
        val clampedValue = seconds.coerceIn(10, 60)
        prefs.edit().putInt(KEY_DEFAULT_HOLD_SECONDS, clampedValue).apply()
        _defaultHoldSeconds.value = clampedValue
    }
    
    private fun getInitialHoldSeconds(): Int {
        return prefs.getInt(KEY_DEFAULT_HOLD_SECONDS, DEFAULT_HOLD_SECONDS)
    }
    
    // ============================================
    // UTILITY FUNCTIONS
    // ============================================
    
    /**
     * Reset all settings to defaults
     */
    fun resetToDefaults() {
        prefs.edit().clear().apply()
        
        // Reset all state flows
        _isDarkTheme.value = false
        _defaultSoundUri.value = getDefaultSoundUri()
        _defaultSoundName.value = getDefaultSoundName()
        _defaultTaskType.value = TaskType.STEPS
        _vibrationEnabled.value = true
        _snoozeDurationMinutes.value = DEFAULT_SNOOZE_DURATION
        _maxSnoozeCount.value = DEFAULT_MAX_SNOOZE_COUNT
        _defaultStepCount.value = DEFAULT_STEP_COUNT
        _defaultDelaySeconds.value = DEFAULT_DELAY_SECONDS
        _defaultHoldSeconds.value = DEFAULT_HOLD_SECONDS
    }
}

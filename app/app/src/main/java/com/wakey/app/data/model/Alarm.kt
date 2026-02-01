package com.wakey.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents an alarm entity in the database.
 * Each alarm has a specific time, repeat days, task type, and settings.
 */
@Entity(tableName = "alarms")
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    /**
     * Hour of the alarm (0-23)
     */
    val hour: Int,
    
    /**
     * Minute of the alarm (0-59)
     */
    val minute: Int,
    
    /**
     * Whether the alarm is currently enabled
     */
    val isEnabled: Boolean = true,
    
    /**
     * Days of the week when the alarm should repeat.
     * Stored as comma-separated values: "0,1,2,3,4,5,6"
     * where 0=Sunday, 1=Monday, ..., 6=Saturday.
     * Empty string means one-time alarm.
     */
    val repeatDays: String = "",
    
    /**
     * Type of task required to dismiss the alarm
     */
    val taskType: TaskType = TaskType.TIME_DELAY,
    
    /**
     * URI of the alarm ringtone
     */
    val ringtoneUri: String = "",
    
    /**
     * Label/name for the alarm (optional)
     */
    val label: String = "",
    
    /**
     * Timestamp when the alarm was created
     */
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Returns the formatted time string (HH:MM)
     */
    fun getFormattedTime(): String {
        return String.format("%02d:%02d", hour, minute)
    }
    
    /**
     * Returns list of repeat day indices
     */
    fun getRepeatDaysList(): List<Int> {
        return if (repeatDays.isEmpty()) {
            emptyList()
        } else {
            repeatDays.split(",").map { it.toInt() }
        }
    }
    
    /**
     * Checks if this is a one-time alarm (no repeat days)
     */
    fun isOneTime(): Boolean = repeatDays.isEmpty()
    
    /**
     * Returns human-readable repeat days string
     */
    fun getRepeatDaysText(): String {
        if (isOneTime()) return "One time"
        
        val days = getRepeatDaysList()
        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        
        return when {
            days.size == 7 -> "Every day"
            days == listOf(1, 2, 3, 4, 5) -> "Weekdays"
            days == listOf(0, 6) -> "Weekends"
            else -> days.map { dayNames[it] }.joinToString(", ")
        }
    }
}

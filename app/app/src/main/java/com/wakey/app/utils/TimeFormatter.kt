package com.wakey.app.utils

import android.content.Context
import android.text.format.DateFormat
import com.wakey.app.data.model.Alarm
import java.util.Calendar

/**
 * Utility functions for formatting time displays.
 */
object TimeFormatter {
    
    /**
     * Format time according to device's 12/24 hour setting.
     * Returns time string and period (AM/PM) if applicable.
     */
    fun formatTime(context: Context, hour: Int, minute: Int): Pair<String, String> {
        val is24Hour = DateFormat.is24HourFormat(context)
        
        return if (is24Hour) {
            // 24-hour format: "00:32"
            val timeString = String.format("%02d:%02d", hour, minute)
            Pair(timeString, "")
        } else {
            // 12-hour format: "12:32" + "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            val period = if (hour < 12) "AM" else "PM"
            val timeString = String.format("%02d:%02d", displayHour, minute)
            Pair(timeString, period)
        }
    }
    
    /**
     * Check if device is using 24-hour format.
     */
    fun is24HourFormat(context: Context): Boolean {
        return DateFormat.is24HourFormat(context)
    }
    
    /**
     * Calculate milliseconds until alarm fires.
     * Returns null if alarm is disabled or has no valid next trigger time.
     */
    fun getTimeUntilAlarm(alarm: Alarm): Long? {
        if (!alarm.isEnabled) return null
        
        val now = Calendar.getInstance()
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val repeatDays = alarm.getRepeatDaysList()
        
        if (repeatDays.isEmpty()) {
            // One-time alarm
            if (alarmTime.before(now) || alarmTime == now) {
                // If time has passed today, set for tomorrow
                alarmTime.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            // Repeating alarm - find next valid day
            val currentDayOfWeek = now.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday
            var daysToAdd = 0
            var found = false
            
            // Check next 7 days
            for (i in 0..6) {
                val checkDay = (currentDayOfWeek + i) % 7
                if (repeatDays.contains(checkDay)) {
                    if (i == 0) {
                        // Today - check if time hasn't passed
                        if (alarmTime.after(now)) {
                            daysToAdd = 0
                            found = true
                            break
                        }
                    } else {
                        daysToAdd = i
                        found = true
                        break
                    }
                }
            }
            
            // If today was checked but time passed, find next occurrence
            if (!found) {
                for (i in 1..7) {
                    val checkDay = (currentDayOfWeek + i) % 7
                    if (repeatDays.contains(checkDay)) {
                        daysToAdd = i
                        break
                    }
                }
            }
            
            alarmTime.add(Calendar.DAY_OF_YEAR, daysToAdd)
        }
        
        return alarmTime.timeInMillis - now.timeInMillis
    }
    
    /**
     * Format time until alarm as human-readable string.
     * Example: "7h 30m", "45m", "23h 59m"
     */
    fun formatTimeUntil(millis: Long): String {
        if (millis <= 0) return "now"
        
        val totalMinutes = (millis / (1000 * 60)).toInt()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        
        return when {
            hours == 0 -> "${minutes}m"
            minutes == 0 -> "${hours}h"
            else -> "${hours}h ${minutes}m"
        }
    }
    
    /**
     * Get the next alarm from a list of alarms.
     * Returns the alarm that will fire soonest, or null if no active alarms.
     */
    fun getNextAlarm(alarms: List<Alarm>): Pair<Alarm, Long>? {
        return alarms
            .filter { it.isEnabled }
            .mapNotNull { alarm ->
                getTimeUntilAlarm(alarm)?.let { time -> alarm to time }
            }
            .minByOrNull { it.second }
    }
    
    /**
     * Format a message for toast/display when alarm is set.
     * Example: "Alarm set for 7h 30m from now"
     */
    fun getAlarmSetMessage(alarm: Alarm): String {
        val timeUntil = getTimeUntilAlarm(alarm) ?: return "Alarm set"
        val formatted = formatTimeUntil(timeUntil)
        return "Alarm in $formatted"
    }
    
    /**
     * Format time until alarm from hour and minute.
     * Used in AlarmEditScreen to show "Alarm in Xh Ym" preview.
     */
    fun formatTimeUntilAlarm(hour: Int, minute: Int): String {
        val now = Calendar.getInstance()
        val alarmTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // If time has passed today, assume tomorrow
        if (alarmTime.before(now) || alarmTime == now) {
            alarmTime.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        val millis = alarmTime.timeInMillis - now.timeInMillis
        return formatTimeUntil(millis)
    }
}

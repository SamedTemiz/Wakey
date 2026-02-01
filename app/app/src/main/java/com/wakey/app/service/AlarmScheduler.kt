package com.wakey.app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.wakey.app.data.model.Alarm
import com.wakey.app.receiver.AlarmReceiver
import java.util.Calendar

/**
 * AlarmScheduler handles scheduling and canceling alarms using AlarmManager.
 * Supports both one-time and repeating alarms.
 */
class AlarmScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    companion object {
        private const val TAG = "AlarmScheduler"
    }
    
    /**
     * Schedule an alarm using AlarmManager.
     * Uses setAlarmClock() for exact timing and lock screen display.
     */
    fun scheduleAlarm(alarm: Alarm) {
        if (!alarm.isEnabled) {
            Log.d(TAG, "Alarm ${alarm.id} is disabled, skipping schedule")
            return
        }
        
        // Check permission on Android 12+
        if (!canScheduleExactAlarms()) {
            Log.e(TAG, "Cannot schedule exact alarms - permission not granted")
            return
        }
        
        val triggerTime = calculateNextAlarmTime(alarm)
        val operationIntent = createPendingIntent(alarm) // Triggers AlarmReceiver
        val showIntent = createShowIntent() // Opens App
        
        // Create AlarmClockInfo for showing on lock screen
        val alarmClockInfo = AlarmManager.AlarmClockInfo(
            triggerTime,
            showIntent // Show intent when user taps clock on lock screen
        )
        
        try {
            alarmManager.setAlarmClock(alarmClockInfo, operationIntent)
            Log.d(TAG, "Scheduled alarm ${alarm.id} for ${formatTime(triggerTime)}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Permission denied for scheduling exact alarm", e)
        }
    }
    
    /**
     * Cancel a scheduled alarm.
     */
    fun cancelAlarm(alarmId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
        
        Log.d(TAG, "Cancelled alarm $alarmId")
    }
    
    /**
     * Reschedule all enabled alarms (used after boot).
     */
    fun rescheduleAllAlarms(alarms: List<Alarm>) {
        Log.d(TAG, "Rescheduling ${alarms.size} alarms")
        alarms.filter { it.isEnabled }.forEach { alarm ->
            scheduleAlarm(alarm)
        }
    }
    
    /**
     * Calculate the next trigger time for an alarm.
     * For repeating alarms, finds the next matching day.
     * For one-time alarms, schedules for today or tomorrow.
     */
    private fun calculateNextAlarmTime(alarm: Alarm): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val now = System.currentTimeMillis()
        
        // If alarm time has passed today, start from tomorrow
        if (calendar.timeInMillis <= now) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        // Handle repeating alarms
        val repeatDays = alarm.getRepeatDaysList()
        if (repeatDays.isNotEmpty()) {
            // Find next matching day
            var attempts = 0
            while (attempts < 7) {
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                val calendarDay = convertToRepeatDay(dayOfWeek)
                
                if (calendarDay in repeatDays && calendar.timeInMillis > now) {
                    break
                }
                
                calendar.add(Calendar.DAY_OF_YEAR, 1)
                attempts++
            }
        }
        
        return calendar.timeInMillis
    }
    
    /**
     * Convert Calendar.DAY_OF_WEEK to our repeat day format.
     * Calendar: Sunday=1, Monday=2, ... Saturday=7
     * Our format: Monday=0, Tuesday=1, ... Sunday=6
     */
    private fun convertToRepeatDay(calendarDay: Int): Int {
        return when (calendarDay) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> 5
            Calendar.SUNDAY -> 6
            else -> 0
        }
    }
    
    /**
     * Create PendingIntent for alarm receiver.
     */
    private fun createPendingIntent(alarm: Alarm): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ALARM_ID, alarm.id)
            addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        }
        
        return PendingIntent.getBroadcast(
            context,
            alarm.id, // Use alarm ID as request code for uniqueness
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Create PendingIntent to open the app (for lock screen icon).
     */
    private fun createShowIntent(): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) 
            ?: Intent(context, com.wakey.app.MainActivity::class.java)
        
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    /**
     * Format time in milliseconds to readable string for logging.
     */
    private fun formatTime(timeMillis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timeMillis
        }
        return String.format(
            "%02d:%02d on %d/%d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH) + 1
        )
    }
    
    /**
     * Check if app has permission to schedule exact alarms (API 31+).
     */
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }
    }
}

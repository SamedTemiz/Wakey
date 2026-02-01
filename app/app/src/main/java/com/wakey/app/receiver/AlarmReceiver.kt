package com.wakey.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.wakey.app.service.AlarmService

/**
 * Broadcast receiver for handling alarm triggers.
 * Called by AlarmManager when an alarm is scheduled to ring.
 * Launches AlarmRingingActivity directly (no notification).
 */
class AlarmReceiver : BroadcastReceiver() {
    
    companion object {
        const val TAG = "AlarmReceiver"
        const val EXTRA_ALARM_ID = "extra_alarm_id"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra(EXTRA_ALARM_ID, -1)
        
        Log.d(TAG, "Alarm received: alarmId=$alarmId")
        
        if (alarmId != -1) {
            // Start AlarmService for sound, vibration, and UI handling
            // The Service will handle the FullScreenIntent to wake the device/show UI
            Log.d(TAG, "Starting AlarmService service for ID: $alarmId")
            AlarmService.startAlarm(context, alarmId)
        }
    }
}

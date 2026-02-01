package com.wakey.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.room.Room
import com.wakey.app.data.AlarmDatabase
import com.wakey.app.service.AlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Broadcast receiver for device boot completed.
 * Reschedules all enabled alarms after device restart.
 */
class BootReceiver : BroadcastReceiver() {
    
    companion object {
        const val TAG = "BootReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed, rescheduling alarms")
            
            // Use coroutine to access database
            val pendingResult = goAsync()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val database = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmDatabase::class.java,
                        AlarmDatabase.DATABASE_NAME
                    ).build()
                    
                    val alarms = database.alarmDao().getAllAlarmsOnce()
                    
                    val scheduler = AlarmScheduler(context)
                    scheduler.rescheduleAllAlarms(alarms)
                    
                    Log.d(TAG, "Rescheduled ${alarms.filter { it.isEnabled }.size} alarms")
                } catch (e: Exception) {
                    Log.e(TAG, "Error rescheduling alarms", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

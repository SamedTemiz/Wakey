package com.wakey.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.wakey.app.R
import com.wakey.app.ui.alarmring.AlarmRingingActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Foreground service that plays alarm sound and vibrates phone.
 * Runs while alarm is ringing to ensure it's not killed by system.
 */
@dagger.hilt.android.AndroidEntryPoint
class AlarmService : Service() {

    @Inject
    lateinit var alarmRepository: com.wakey.app.data.repository.AlarmRepository

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var currentAlarmId: Int = -1

    companion object {
        private const val TAG = "AlarmService"

        // Changed ID to force new channel creation with High Priority
        private const val CHANNEL_ID = "alarm_service_channel_high_priority"
        private const val NOTIFICATION_ID = 100

        const val ACTION_START_ALARM = "com.wakey.app.START_ALARM"
        const val ACTION_STOP_ALARM = "com.wakey.app.STOP_ALARM"
        const val EXTRA_ALARM_ID = "alarm_id"

        /**
         * Start the alarm service
         */
        fun startAlarm(context: Context, alarmId: Int) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_START_ALARM
                putExtra(EXTRA_ALARM_ID, alarmId)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        /**
         * Stop the alarm service
         */
        fun stopAlarm(context: Context) {
            val intent = Intent(context, AlarmService::class.java).apply {
                action = ACTION_STOP_ALARM
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AlarmService created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_ALARM -> {
                val alarmId = intent.getIntExtra(EXTRA_ALARM_ID, -1)
                
                // If same alarm is already running, do nothing (prevents Activity restart)
                if (alarmId != -1 && alarmId == currentAlarmId) {
                    Log.d(TAG, "Same alarm $alarmId already running, skipping")
                    return START_NOT_STICKY
                }
                
                if (alarmId != -1) currentAlarmId = alarmId
                Log.d(TAG, "Starting alarm $alarmId")
                startForeground(NOTIFICATION_ID, createNotification())
                startAlarmSound()
                startVibration()
            }

            ACTION_STOP_ALARM -> {
                Log.d(TAG, "Stopping alarm")
                stopAlarmSound()
                stopVibration()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopAlarmSound()
        stopVibration()
        Log.d(TAG, "AlarmService destroyed")
    }

    /**
     * Create notification for foreground service
     */
    private fun createNotification(): Notification {
        // Full Screen Intent (opens Activity immediately if screen is off/locked)
        val fullScreenIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            if (currentAlarmId != -1) {
                putExtra("alarmId", currentAlarmId)
            }
            // These flags are crucial for showing over lock screen
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_NO_USER_ACTION or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingFullScreenIntent = PendingIntent.getActivity(
            this,
            currentAlarmId * 10, // Distinct request code
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Content Intent (Triggered when user taps the notification)
        // Uses the same Intent to bring the existing singleTask activity to front
        val pendingContentIntent = PendingIntent.getActivity(
            this,
            currentAlarmId * 10 + 1, // Distinct from fullScreen
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm Ringing")
            .setContentText("Tap to dimiss or snooze")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_MAX) // MAX priority for interruption
            .setCategory(NotificationCompat.CATEGORY_ALARM) // ALARM category allows DND bypass
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Show on lock screen
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingContentIntent) // Direct to Activity
            .setFullScreenIntent(pendingFullScreenIntent, true)

        return builder.build()
    }

    /**
     * Create notification channel for foreground service
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Ringing Service",
                NotificationManager.IMPORTANCE_HIGH // HIGH importance for heads-up notification
            ).apply {
                description = "Shows when alarm is ringing"
                setSound(null, null) // Sound is handled by MediaPlayer, not Notification
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Updated startAlarmSound to be suspending or use scope
    private fun startAlarmSound() {
        // We need to fetch the alarm to get the custom ringtone
        // Since we are in a Service, we launch a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            var alarmUri: android.net.Uri? = null

            if (currentAlarmId != -1) {
                val alarm = alarmRepository.getAlarmById(currentAlarmId)
                if (alarm != null && alarm.ringtoneUri.isNotEmpty()) {
                    try {
                        alarmUri = android.net.Uri.parse(alarm.ringtoneUri)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing ringtone URI", e)
                    }
                }
            }

            // Fallback to default
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                    ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            }

            withContext(Dispatchers.Main) {
                try {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(this@AlarmService, alarmUri!!)

                        setAudioAttributes(
                            AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_ALARM)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build()
                        )

                        isLooping = true
                        prepare()
                        start()
                    }
                    Log.d(TAG, "Alarm sound started: $alarmUri")
                } catch (e: Exception) {
                    Log.e(TAG, "Error playing alarm sound", e)
                }
            }
        }
    }

    /**
     * Stop alarm sound
     */
    private fun stopAlarmSound() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        Log.d(TAG, "Alarm sound stopped")
    }

    /**
     * Start vibration pattern
     */
    private fun startVibration() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Vibration pattern: [delay, vibrate, sleep, vibrate, ...]
        val pattern = longArrayOf(0, 1000, 1000, 1000, 1000)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(pattern, 0) // 0 = repeat
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }

        Log.d(TAG, "Vibration started")
    }

    /**
     * Stop vibration
     */
    private fun stopVibration() {
        vibrator?.cancel()
        vibrator = null
        Log.d(TAG, "Vibration stopped")
    }
}

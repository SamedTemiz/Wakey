package com.wakey.app.ui.alarmring.tasks

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.abs

/**
 * Vertical hold task using TYPE_ACCELEROMETER sensor.
 * Requires holding phone vertical for 20 seconds.
 */
class VerticalHoldTask(private val context: Context) {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    /**
     * Flow that emits whether device is currently held vertically.
     * true = vertical, false = not vertical
     */
    fun observeVerticalState(): Flow<Boolean> = callbackFlow {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    
                    // Check if phone is vertical
                    // When vertical: Z should be close to 9.8 (gravity), X and Y close to 0
                    val isVertical = abs(z) > 8.0 && abs(x) < 3.0 && abs(y) < 3.0
                    
                    trySend(isVertical)
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed
            }
        }
        
        if (accelerometer != null) {
            sensorManager.registerListener(
                listener,
                accelerometer,
                SensorManager.SENSOR_DELAY_UI
            )
        } else {
            // No accelerometer available
            trySend(false)
        }
        
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
    
    /**
     * Check if accelerometer is available on this device
     */
    fun isAvailable(): Boolean {
        return accelerometer != null
    }
}

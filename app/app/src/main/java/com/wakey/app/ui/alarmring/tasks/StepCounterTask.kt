package com.wakey.app.ui.alarmring.tasks

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Step counter task using TYPE_STEP_COUNTER sensor.
 * Counts 30 steps to dismiss alarm.
 */
class StepCounterTask(private val context: Context) {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    
    /**
     * Flow that emits step count deltas.
     * Emits the number of steps taken since task started.
     */
    fun observeSteps(): Flow<Int> = callbackFlow {
        var initialSteps: Int? = null
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
                    val totalSteps = event.values[0].toInt()
                    
                    if (initialSteps == null) {
                        initialSteps = totalSteps
                    }
                    
                    val stepsTaken = totalSteps - initialSteps!!
                    trySend(stepsTaken.coerceAtMost(30))
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not needed
            }
        }
        
        if (stepSensor != null) {
            sensorManager.registerListener(
                listener,
                stepSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        } else {
            // No step counter available
            trySend(-1)
        }
        
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
    
    /**
     * Check if step counter is available on this device
     */
    fun isAvailable(): Boolean {
        return stepSensor != null
    }
}

package com.wakey.app.data.model

/**
 * Represents the type of task that must be completed to dismiss the alarm.
 */
enum class TaskType {
    /**
     * User must walk a certain number of steps to dismiss the alarm.
     * Uses the device's step counter sensor.
     */
    STEPS,
    
    /**
     * User must hold the phone vertically for a certain duration.
     * Uses the device's accelerometer sensor.
     */
    HOLD_VERTICAL,
    
    /**
     * User must wait for a countdown timer to complete.
     * Simple time delay before dismissal.
     */
    TIME_DELAY
}

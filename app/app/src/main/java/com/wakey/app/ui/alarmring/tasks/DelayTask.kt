package com.wakey.app.ui.alarmring.tasks

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Delay task - simple timer countdown.
 * User must wait 15 seconds to dismiss alarm.
 */
class DelayTask {
    
    /**
     * Flow that emits elapsed seconds every second.
     * Counts from 0 to 15.
     */
    fun observeTimer(): Flow<Int> = flow {
        for (second in 0..15) {
            emit(second)
            if (second < 15) {
                delay(1000) // Wait 1 second
            }
        }
    }
}

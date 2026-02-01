package com.wakey.app.utils

/**
 * Singleton to track the state of the alarm ringing activity.
 * Used to decide whether notification click should launch activity or do nothing.
 */
object AlarmStateManager {
    var isRingingActivityVisible: Boolean = false
}

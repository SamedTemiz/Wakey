package com.wakey.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Application class for Wakey.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class WakeyApp : Application()

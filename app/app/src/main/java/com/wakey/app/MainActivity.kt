package com.wakey.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.wakey.app.ui.navigation.AppNavGraph
import com.wakey.app.ui.theme.WakeyTheme
import com.wakey.app.utils.PermissionHelper
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main entry point of the Wakey application.
 * Annotated with @AndroidEntryPoint to enable Hilt dependency injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @javax.inject.Inject
    lateinit var settingsRepository: com.wakey.app.data.repository.SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WakeyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Main app navigation
                    val navController = rememberNavController()
                    AppNavGraph(navController = navController)

                    // Check exact alarm permission on Android 12+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PermissionCheckDialog()
                    }
                }
            }
        }
    }

    /**
     * Dialog to request exact alarm permission on Android 12+
     */
    @Composable
    fun PermissionCheckDialog() {
        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }
        var hasChecked by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (!hasChecked) {
                hasChecked = true
                showDialog = !PermissionHelper.canScheduleExactAlarms(context)
            }
        }

        if (showDialog) {

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Alarm Permission Required") },
                text = {
                    Text(
                        "This app needs permission to schedule exact alarms. " +
                                "Please enable \"Alarms & reminders\" in settings."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            PermissionHelper.openExactAlarmSettings(context)
                        }
                        showDialog = false
                    }) {
                        Text("Open Settings")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Later")
                    }
                }
            )
        }
    }
}

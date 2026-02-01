package com.wakey.app.ui.alarmlist

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wakey.app.data.model.Alarm
import com.wakey.app.data.model.TaskType
import com.wakey.app.ui.theme.AlarmActiveColor
import com.wakey.app.ui.theme.AlarmInactiveColor
import com.wakey.app.ui.theme.DelayTaskColor
import com.wakey.app.ui.theme.PrimaryCoral
import com.wakey.app.ui.theme.StepsTaskColor
import com.wakey.app.ui.theme.SwitchOffColor
import com.wakey.app.ui.theme.SwitchOnColor
import com.wakey.app.ui.theme.TextSecondary
import com.wakey.app.ui.theme.VerticalTaskColor
import com.wakey.app.ui.components.AlarmListBottomBar
import com.wakey.app.ui.components.AlarmCard as AlarmCardComponent
import com.wakey.app.ui.components.getTaskIcon

/**
 * Main screen showing the list of alarms.
 * Design based on provided mockups.
 * 
 * @param onAddAlarm Callback when user wants to add a new alarm
 * @param onEditAlarm Callback when user wants to edit an alarm (passes alarm ID)
 * @param onNavigateToSettings Callback when user taps settings icon
 * @param viewModel ViewModel for managing alarm list state
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmListScreen(
    onAddAlarm: () -> Unit,
    onEditAlarm: (Int) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    viewModel: AlarmListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val textSecondary = TextSecondary
    val context = LocalContext.current
    
    // Show toast when alarm is toggled
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding() // Fix status bar overlap
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header - Pencil: padding [16, 24], space_between
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Pencil: title + subtitle, gap 4
                Column {
                    // Pencil: Montserrat 32px weight 300
                    Text(
                        text = "Alarms",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Pencil: Inter 13px
                    Text(
                        text = getNextAlarmText(uiState.alarms),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 13.sp
                        ),
                        color = textSecondary
                    )
                }
                
                // Right side - Pencil: more_vert icon 24x24
                IconButton(onClick = { /* Menu action */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = textSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            // Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = PrimaryCoral
                        )
                    }
                    uiState.alarms.isEmpty() -> {
                        EmptyAlarmList(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }
                            
                            items(
                                items = uiState.alarms,
                                key = { it.id }
                            ) { alarm ->
                                val context = LocalContext.current
                                val (timeString, period) = com.wakey.app.utils.TimeFormatter.formatTime(
                                    context, alarm.hour, alarm.minute
                                )
                                
                                AlarmCardComponent(
                                    time = timeString,
                                    period = period,
                                    schedule = alarm.getRepeatDaysText(),
                                    taskIcon = getTaskIcon(alarm.taskType.name),
                                    taskDescription = getTaskDescription(alarm.taskType),
                                    isActive = alarm.isEnabled,
                                    onToggle = { viewModel.toggleAlarm(alarm) },
                                    onClick = { onEditAlarm(alarm.id) }
                                )
                            }
                            
                            // Extra space for bottom bar
                            item { Spacer(modifier = Modifier.height(100.dp)) }
                        }
                    }
                }
            }
        }
        
        // Bottom Bar - positioned at bottom
        AlarmListBottomBar(
            onAddClick = {
                if (viewModel.checkCanAddAlarm()) {
                    onAddAlarm()
                }
            },
            onSettingsClick = onNavigateToSettings,
            canAddAlarm = uiState.canAddAlarm,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}

/**
 * Get text for next alarm
 */
private fun getNextAlarmText(alarms: List<Alarm>): String {
    val nextAlarm = com.wakey.app.utils.TimeFormatter.getNextAlarm(alarms)
    
    return if (nextAlarm != null) {
        val (_, timeUntil) = nextAlarm
        val formatted = com.wakey.app.utils.TimeFormatter.formatTimeUntil(timeUntil)
        "Next wake up in $formatted"
    } else {
        "No active alarms"
    }
}

/**
 * Get task description based on task type
 */
private fun getTaskDescription(taskType: TaskType): String {
    return when (taskType) {
        TaskType.STEPS -> "Steps: 30 steps"
        TaskType.HOLD_VERTICAL -> "Hold: 20 seconds"
        TaskType.TIME_DELAY -> "Wait: 15 seconds"
    }
}

/**
 * Empty state when no alarms are configured
 */
@Composable
private fun EmptyAlarmList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Alarm,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Alarms Yet",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap + to add your first alarm",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            textAlign = TextAlign.Center
        )
    }
}

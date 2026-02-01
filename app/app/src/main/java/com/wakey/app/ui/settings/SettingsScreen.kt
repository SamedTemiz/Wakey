package com.wakey.app.ui.settings

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wakey.app.data.model.TaskType
import com.wakey.app.ui.components.WakeUpSwitch
import com.wakey.app.ui.theme.AccentOrange
import com.wakey.app.ui.theme.CardBackground
import com.wakey.app.ui.theme.DelayTaskColor
import com.wakey.app.ui.theme.PrimaryCoral
import com.wakey.app.ui.theme.StepsTaskColor
import com.wakey.app.ui.theme.TextSecondary
import com.wakey.app.ui.theme.VerticalTaskColor

/**
 * Settings Screen for Wakey app.
 * Allows users to configure default alarm settings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    
    // Ringtone picker launcher
    val ringtoneLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            if (uri != null) {
                val ringtone = RingtoneManager.getRingtone(context, uri)
                val name = ringtone?.getTitle(context) ?: "Custom"
                viewModel.setDefaultSound(uri.toString(), name)
            }
        }
    }
    
    // Dialog states
    var showTaskTypeDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showSnoozeDialog by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryCoral
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header - Pencil: back + "Settings" (Montserrat 20px 600) + spacer, padding [8, 16]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Back button - Pencil: 24x24
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    // Title - Pencil: Montserrat 20px weight 600
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    // Spacer to balance the header
                    Spacer(modifier = Modifier.size(24.dp))
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // ============================================
                // ALARM DEFAULTS SECTION
                // ============================================
                
                SectionHeader(title = "ALARM DEFAULTS")
                
                SettingsCard {
                    // Default Sound
                    SettingsRow(
                        icon = Icons.Default.MusicNote,
                        iconColor = AccentOrange,
                        title = "Default Sound",
                        subtitle = uiState.defaultSoundName,
                        onClick = {
                            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                                if (uiState.defaultSoundUri.isNotEmpty()) {
                                    putExtra(
                                        RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                                        Uri.parse(uiState.defaultSoundUri)
                                    )
                                }
                            }
                            ringtoneLauncher.launch(intent)
                        },
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    )
                    
                    SettingsDivider()
                    
                    // Default Task Type
                    SettingsRow(
                        icon = getTaskIcon(uiState.defaultTaskType),
                        iconColor = getTaskColor(uiState.defaultTaskType),
                        title = "Default Task",
                        subtitle = getTaskDisplayName(uiState.defaultTaskType),
                        onClick = { showTaskTypeDialog = true },
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    )
                    
                    SettingsDivider()
                    
                    // Vibration Toggle
                    SettingsRow(
                        icon = Icons.Default.Vibration,
                        iconColor = PrimaryCoral,
                        title = "Vibration",
                        subtitle = if (uiState.vibrationEnabled) "Enabled" else "Disabled",
                        onClick = { viewModel.toggleVibration() },
                        trailing = {
                            WakeUpSwitch(
                                checked = uiState.vibrationEnabled,
                                onCheckedChange = { viewModel.setVibrationEnabled(it) }
                            )
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // ============================================
                // SNOOZE SETTINGS SECTION
                // ============================================
                
                SectionHeader(title = "SNOOZE")
                
                SettingsCard {
                    // Snooze Duration
                    SettingsRow(
                        icon = Icons.Default.Snooze,
                        iconColor = DelayTaskColor,
                        title = "Snooze Duration",
                        subtitle = "${uiState.snoozeDurationMinutes} minutes",
                        onClick = { showSnoozeDialog = true },
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // ============================================
                // TASK SETTINGS SECTION
                // ============================================
                
                SectionHeader(title = "TASK DEFAULTS")
                
                SettingsCard {
                    // Step Count
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SettingsIconBox(
                                    icon = Icons.Default.DirectionsWalk,
                                    color = StepsTaskColor
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Steps Required",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${uiState.defaultStepCount}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = StepsTaskColor
                            )
                        }
                        
                        Slider(
                            value = uiState.defaultStepCount.toFloat(),
                            onValueChange = { viewModel.setDefaultStepCount(it.toInt()) },
                            valueRange = 10f..100f,
                            steps = 8,
                            modifier = Modifier.padding(top = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = StepsTaskColor,
                                activeTrackColor = StepsTaskColor
                            )
                        )
                    }
                    
                    SettingsDivider()
                    
                    // Delay Seconds
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SettingsIconBox(
                                    icon = Icons.Default.Timer,
                                    color = DelayTaskColor
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Wait Timer",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${uiState.defaultDelaySeconds}s",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = DelayTaskColor
                            )
                        }
                        
                        Slider(
                            value = uiState.defaultDelaySeconds.toFloat(),
                            onValueChange = { viewModel.setDefaultDelaySeconds(it.toInt()) },
                            valueRange = 5f..60f,
                            steps = 10,
                            modifier = Modifier.padding(top = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = DelayTaskColor,
                                activeTrackColor = DelayTaskColor
                            )
                        )
                    }
                    
                    SettingsDivider()
                    
                    // Hold Vertical Seconds
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                SettingsIconBox(
                                    icon = Icons.Default.PhoneAndroid,
                                    color = VerticalTaskColor
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Hold Vertical",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Text(
                                text = "${uiState.defaultHoldSeconds}s",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = VerticalTaskColor
                            )
                        }
                        
                        Slider(
                            value = uiState.defaultHoldSeconds.toFloat(),
                            onValueChange = { viewModel.setDefaultHoldSeconds(it.toInt()) },
                            valueRange = 10f..60f,
                            steps = 9,
                            modifier = Modifier.padding(top = 8.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = VerticalTaskColor,
                                activeTrackColor = VerticalTaskColor
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // ============================================
                // ABOUT SECTION
                // ============================================
                
                SectionHeader(title = "ABOUT")
                
                SettingsCard {
                    // App Version
                    SettingsRow(
                        icon = Icons.Default.Info,
                        iconColor = TextSecondary,
                        title = "Version",
                        subtitle = uiState.appVersion,
                        onClick = null,
                        trailing = null
                    )
                    
                    SettingsDivider()
                    
                    // Privacy Policy
                    SettingsRow(
                        icon = Icons.Default.Policy,
                        iconColor = TextSecondary,
                        title = "Privacy Policy",
                        subtitle = "View our privacy policy",
                        onClick = {
                            uriHandler.openUri(viewModel.getPrivacyPolicyUrl())
                        },
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    )
                    
                    SettingsDivider()
                    
                    // Reset to Defaults
                    SettingsRow(
                        icon = Icons.Default.RestartAlt,
                        iconColor = PrimaryCoral,
                        title = "Reset to Defaults",
                        subtitle = "Restore all settings",
                        onClick = { showResetDialog = true },
                        trailing = {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                        }
                    )
                }
                
                // Extra bottom spacing
                Spacer(modifier = Modifier.height(32.dp))
                
                // Navigation bar padding for button navigation phones
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
    
    // ============================================
    // DIALOGS
    // ============================================
    
    // Task Type Selection Dialog
    if (showTaskTypeDialog) {
        TaskTypeDialog(
            currentTaskType = uiState.defaultTaskType,
            onTaskTypeSelected = { taskType ->
                viewModel.setDefaultTaskType(taskType)
                showTaskTypeDialog = false
            },
            onDismiss = { showTaskTypeDialog = false }
        )
    }
    
    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings?") },
            text = { Text("This will restore all settings to their default values.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetToDefaults()
                        showResetDialog = false
                    }
                ) {
                    Text("Reset", color = PrimaryCoral)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    
    // Snooze Duration Dialog
    if (showSnoozeDialog) {
        SnoozeDurationDialog(
            currentDuration = uiState.snoozeDurationMinutes,
            onDurationSelected = { minutes ->
                viewModel.setSnoozeDuration(minutes)
                showSnoozeDialog = false
            },
            onDismiss = { showSnoozeDialog = false }
        )
    }
}

// ============================================
// HELPER COMPOSABLES
// ============================================

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)?,
    trailing: (@Composable () -> Unit)?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SettingsIconBox(icon = icon, color = iconColor)
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
        
        trailing?.invoke()
    }
}

@Composable
private fun SettingsIconBox(
    icon: ImageVector,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun SettingsDivider() {
    Divider(
        modifier = Modifier.padding(start = 72.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        thickness = 0.5.dp
    )
}

// ============================================
// DIALOGS
// ============================================

@Composable
private fun TaskTypeDialog(
    currentTaskType: TaskType,
    onTaskTypeSelected: (TaskType) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Default Task") },
        text = {
            Column {
                TaskType.values().forEach { taskType ->
                    TaskTypeOption(
                        taskType = taskType,
                        isSelected = taskType == currentTaskType,
                        onClick = { onTaskTypeSelected(taskType) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun TaskTypeOption(
    taskType: TaskType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = getTaskIcon(taskType),
            contentDescription = null,
            tint = getTaskColor(taskType),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = getTaskDisplayName(taskType),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = PrimaryCoral
            )
        }
    }
}

@Composable
private fun SnoozeDurationDialog(
    currentDuration: Int,
    onDurationSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val durations = listOf(1, 3, 5, 10, 15, 20, 30)
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Snooze Duration") },
        text = {
            Column {
                durations.forEach { minutes ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDurationSelected(minutes) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$minutes minutes",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (minutes == currentDuration) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = PrimaryCoral
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// ============================================
// HELPER FUNCTIONS
// ============================================

private fun getTaskIcon(taskType: TaskType): ImageVector {
    return when (taskType) {
        TaskType.STEPS -> Icons.Default.DirectionsWalk
        TaskType.HOLD_VERTICAL -> Icons.Default.PhoneAndroid
        TaskType.TIME_DELAY -> Icons.Default.Timer
    }
}

private fun getTaskColor(taskType: TaskType): Color {
    return when (taskType) {
        TaskType.STEPS -> StepsTaskColor
        TaskType.HOLD_VERTICAL -> VerticalTaskColor
        TaskType.TIME_DELAY -> DelayTaskColor
    }
}

private fun getTaskDisplayName(taskType: TaskType): String {
    return when (taskType) {
        TaskType.STEPS -> "Steps (30 steps)"
        TaskType.HOLD_VERTICAL -> "Hold (20 seconds)"
        TaskType.TIME_DELAY -> "Wait (15 seconds)"
    }
}

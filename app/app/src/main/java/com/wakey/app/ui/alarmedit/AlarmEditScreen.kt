package com.wakey.app.ui.alarmedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.wakey.app.data.model.TaskType
import com.wakey.app.ui.theme.AccentGold
import com.wakey.app.ui.theme.CardBackground
import com.wakey.app.ui.theme.DelayTaskColor
import com.wakey.app.ui.theme.PrimaryCoral
import com.wakey.app.ui.theme.AccentOrange
import com.wakey.app.ui.theme.StepsTaskColor
import com.wakey.app.ui.theme.TextSecondary
import com.wakey.app.ui.theme.VerticalTaskColor
import com.wakey.app.ui.components.SetAlarmBottomBar
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Size as ComposeSize

/**
 * Screen for creating or editing an alarm
 * Design based on provided mockups with circular time display
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AlarmEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: AlarmEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showTimePicker by remember { mutableStateOf(false) }
    val textSecondary = TextSecondary
    val toastContext = androidx.compose.ui.platform.LocalContext.current
    
    // Show toast and navigate back when saved
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            uiState.toastMessage?.let { message ->
                Toast.makeText(toastContext, message, Toast.LENGTH_SHORT).show()
            }
            onNavigateBack()
        }
    }
    
    // Show error
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding() // Fix status bar overlap
    ) {
        // Main content - scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp) // Space for bottom bar
        ) {
            // Header - Pencil: close + "Set Alarm" (Inter 17px 600) + "Reset"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Close button - Pencil: 24x24
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                }
                
                // Title - Pencil: Inter 17px weight 600
                Text(
                    text = "Set Alarm",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                // Reset button - Pencil: Inter 14px normal
                Text(
                    text = "Reset",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = textSecondary,
                    modifier = Modifier.clickable { viewModel.resetToDefaults() }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            /* AM/PM selector - removed for 24-hour format
            // AM/PM selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "AM",
                    fontSize = 16.sp,
                    fontWeight = if (uiState.hour < 12) FontWeight.Bold else FontWeight.Normal,
                    color = if (uiState.hour < 12) PrimaryCoral else TextSecondary,
                    modifier = Modifier
                        .clickable { 
                            if (uiState.hour >= 12) viewModel.setTime(uiState.hour - 12, uiState.minute) 
                        }
                        .padding(8.dp)
                )
                Text(
                    text = " • ",
                    color = textSecondary
                )
                Text(
                    text = "PM",
                    fontSize = 16.sp,
                    fontWeight = if (uiState.hour >= 12) FontWeight.Bold else FontWeight.Normal,
                    color = if (uiState.hour >= 12) PrimaryCoral else textSecondary,
                    modifier = Modifier
                        .clickable { 
                            if (uiState.hour < 12) viewModel.setTime(uiState.hour + 12, uiState.minute) 
                        }
                        .padding(8.dp)
                )
            }
            */
            
            // Time Picker Section - Pencil: padding 24, gap 16
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Time Circle - Pencil: 220x220 with angular gradient stroke
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clickable { showTimePicker = true },
                    contentAlignment = Alignment.Center
                ) {
                    // Gradient ring stroke
                    Canvas(modifier = Modifier.size(220.dp)) {
                        val strokeWidth = 6.dp.toPx()
                        val radius = (size.minDimension - strokeWidth) / 2
                        
                        // Angular gradient: peach → gold → gray
                        drawCircle(
                            brush = Brush.sweepGradient(
                                0f to AccentOrange,
                                0.25f to AccentGold,
                                0.3f to Color(0xFFE4E4E7),
                                1f to Color(0xFFE4E4E7)
                            ),
                            radius = radius,
                            style = Stroke(width = strokeWidth)
                        )
                    }
                    
                    // Time display content - Pencil: vertical layout, gap 4
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Time - Pencil: Montserrat 52px weight 300
                        val context = androidx.compose.ui.platform.LocalContext.current
                        val (timeDisplay, _) = com.wakey.app.utils.TimeFormatter.formatTime(
                            context, uiState.hour, uiState.minute
                        )
                        
                        Text(
                            text = timeDisplay,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 52.sp,
                                fontWeight = FontWeight.Light
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        // Check device time format
                        val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)
                        
                        // AM/PM Toggle - Only show for 12-hour format
                        if (!is24HourFormat) {
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Pencil: cornerRadius 16, fill bg-card, padding 4
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(CardBackground)
                                    .padding(4.dp)
                            ) {
                                // AM button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (uiState.hour < 12) AccentOrange else Color.Transparent)
                                        .clickable { 
                                            if (uiState.hour >= 12) viewModel.setTime(uiState.hour - 12, uiState.minute) 
                                        }
                                        .size(width = 40.dp, height = 28.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "AM",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (uiState.hour < 12) FontWeight.SemiBold else FontWeight.Medium
                                        ),
                                        color = if (uiState.hour < 12) Color.White else textSecondary
                                    )
                                }
                                
                                // PM button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (uiState.hour >= 12) AccentOrange else Color.Transparent)
                                        .clickable { 
                                            if (uiState.hour < 12) viewModel.setTime(uiState.hour + 12, uiState.minute) 
                                        }
                                        .size(width = 40.dp, height = 28.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "PM",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = if (uiState.hour >= 12) FontWeight.SemiBold else FontWeight.Medium
                                        ),
                                        color = if (uiState.hour >= 12) Color.White else textSecondary
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Alarm in label - Pencil: alarm icon + text, gap 6
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Alarm,
                        contentDescription = null,
                        tint = textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    val alarmInText = com.wakey.app.utils.TimeFormatter.formatTimeUntilAlarm(uiState.hour, uiState.minute)
                    Text(
                        text = "Alarm in $alarmInText",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 13.sp
                        ),
                        color = textSecondary
                    )
                }
            }
            
            // ============================================
            // MISSION SECTION - Pencil: comes after Time Picker
            // ============================================
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Mission Header - Pencil: "Mission" + MANDATORY badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mission",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    // MANDATORY badge - Pencil: cornerRadius 8, fill #FEE2E2, padding [4, 8]
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFEE2E2))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "MANDATORY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = PrimaryCoral
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Mission Options - Pencil: horizontal row, gap 12, height 80
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Steps option - Walk 30 steps to dismiss
                    MissionOptionCard(
                        icon = Icons.Default.DirectionsWalk,
                        label = "Steps",
                        isSelected = uiState.taskType == TaskType.STEPS,
                        onClick = { viewModel.setTaskType(TaskType.STEPS) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Hold Vertical option - Hold phone upright for 20 seconds
                    MissionOptionCard(
                        icon = Icons.Default.PhoneAndroid,
                        label = "Hold",
                        isSelected = uiState.taskType == TaskType.HOLD_VERTICAL,
                        onClick = { viewModel.setTaskType(TaskType.HOLD_VERTICAL) },
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Wait option - Wait 15 seconds to dismiss
                    MissionOptionCard(
                        icon = Icons.Default.Timer,
                        label = "Wait",
                        isSelected = uiState.taskType == TaskType.TIME_DELAY,
                        onClick = { viewModel.setTaskType(TaskType.TIME_DELAY) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // ============================================
            // SETTINGS SECTION - Pencil: comes after Mission
            // ============================================
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                // Ringtone Picker setup
                val context = androidx.compose.ui.platform.LocalContext.current
                val ringtoneLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == android.app.Activity.RESULT_OK) {
                        val uri = result.data?.getParcelableExtra<android.net.Uri>(android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
                        if (uri != null) {
                            viewModel.setRingtoneUri(uri.toString())
                        }
                    }
                }
                
                val ringtoneTitle = remember(uiState.ringtoneUri) {
                    if (uiState.ringtoneUri.isEmpty()) "Default"
                    else {
                        try {
                            val ringtone = android.media.RingtoneManager.getRingtone(
                                context, 
                                android.net.Uri.parse(uiState.ringtoneUri)
                            )
                           ringtone.getTitle(context)
                        } catch (e: Exception) {
                            "Custom Ringtone"
                        }
                    }
                }
                
                // Day picker dialog state
                var showDayPickerDialog by remember { mutableStateOf(false) }
                
                // Repeat Row - Pencil: repeat icon + "Repeat" + value + chevron
                // No inline day selector - just shows value like "Mon - Fri"
                SettingsRow(
                    icon = Icons.Default.Repeat,
                    title = "Repeat",
                    value = getRepeatText(uiState.selectedDays),
                    valueColor = AccentOrange,
                    showBorder = true,
                    onClick = { showDayPickerDialog = true }
                )
                
                // Sound Row - Pencil: music_note icon + "Sound" + value + chevron
                SettingsRow(
                    icon = Icons.Default.MusicNote,
                    title = "Sound",
                    value = ringtoneTitle,
                    valueColor = textSecondary,
                    showBorder = true,
                    onClick = {
                        val intent = android.content.Intent(android.media.RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                            putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_TYPE, android.media.RingtoneManager.TYPE_ALARM)
                            putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                            putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                            
                            val currentUri = if (uiState.ringtoneUri.isNotEmpty()) 
                                android.net.Uri.parse(uiState.ringtoneUri) 
                            else 
                                android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_ALARM)
                                
                            putExtra(android.media.RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentUri)
                        }
                        ringtoneLauncher.launch(intent)
                    }
                )
                
                // Label Row - Pencil: label icon + "Label" + value + chevron
                SettingsRow(
                    icon = Icons.Default.Label,
                    title = "Label",
                    value = if (uiState.label.isEmpty()) "Add label" else uiState.label,
                    valueColor = textSecondary,
                    showBorder = false,
                    onClick = { /* TODO: Show label input dialog */ }
                )
                
                // Day Picker Dialog
                if (showDayPickerDialog) {
                    DayPickerDialog(
                        selectedDays = uiState.selectedDays,
                        onDayToggle = viewModel::toggleDay,
                        onDismiss = { showDayPickerDialog = false }
                    )
                }
            }
            
            // Spacer to push content up from bottom bar
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Bottom Bar with curved background
        SetAlarmBottomBar(
            onSetClick = { viewModel.saveAlarm() },
            isLoading = uiState.isLoading,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        
        // Snackbar
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 130.dp)
        )
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            initialHour = uiState.hour,
            initialMinute = uiState.minute,
            onConfirm = { hour, minute ->
                viewModel.setTime(hour, minute)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
}

/**
 * Get repeat text from selected days
 */
private fun getRepeatText(selectedDays: Set<Int>): String {
    return when {
        selectedDays.isEmpty() -> "Once"
        selectedDays.size == 7 -> "Every day"
        selectedDays == setOf(1, 2, 3, 4, 5) -> "Weekdays"
        selectedDays == setOf(0, 6) -> "Weekends"
        else -> "Custom"
    }
}

/**
 * Day picker dialog
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DayPickerDialog(
    selectedDays: Set<Int>,
    onDayToggle: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Repeat",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Day selector
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    days.forEachIndexed { index, day ->
                        val isSelected = selectedDays.contains(index)
                        
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) PrimaryCoral
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { onDayToggle(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.first().toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isSelected) Color.White
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Done button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Done", fontWeight = FontWeight.Bold, color = PrimaryCoral)
                    }
                }
            }
        }
    }
}

/**
 * Setting card component
 */
@Composable
private fun SettingCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        PrimaryCoral.copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PrimaryCoral
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            trailing()
        }
    }
}

/**
 * Settings row - Pencil style
 * Icon 20x20, label Inter 15px, value + chevron on right
 */
@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    value: String,
    valueColor: Color,
    showBorder: Boolean,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: icon + title
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            
            // Right side: value + chevron
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    ),
                    color = valueColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Bottom border
        if (showBorder) {
            Divider(
                color = Color(0xFFE4E4E7),
                thickness = 1.dp
            )
        }
    }
}

/**
 * Mission option card - Pencil style
 * Height 80, cornerRadius 16, vertical layout
 */
@Composable
private fun MissionOptionCard(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Color.White else CardBackground
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onBackground else TextSecondary
    
    Card(
        onClick = onClick,
        modifier = modifier.height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, PrimaryCoral) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Selected checkmark - Pencil: small check badge on top-left
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(PrimaryCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Icon - Pencil: 28x28
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Label - Pencil: Inter 12px weight 500
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = contentColor
                )
            }
        }
    }
}

/**
 * Time picker dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Get device's time format preference
    val context = androidx.compose.ui.platform.LocalContext.current
    val is24Hour = android.text.format.DateFormat.is24HourFormat(context)
    
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                TimePicker(state = timePickerState)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    androidx.compose.material3.TextButton(
                        onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }
                    ) {
                        Text("OK", fontWeight = FontWeight.Bold, color = PrimaryCoral)
                    }
                }
            }
        }
    }
}

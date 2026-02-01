package com.wakey.app.ui.alarmring

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakey.app.data.model.TaskType
import com.wakey.app.MainActivity
import com.wakey.app.service.AlarmService
import com.wakey.app.ui.theme.AccentGold
import com.wakey.app.ui.theme.AccentOrange
import com.wakey.app.ui.theme.CardBackground
import com.wakey.app.ui.theme.PrimaryCoral
import com.wakey.app.ui.theme.TextPrimary
import com.wakey.app.ui.theme.TextSecondary
import com.wakey.app.ui.theme.WakeyTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Full-screen activity shown when alarm rings.
 * Displays task UI and blocks dismissal until task is complete.
 */
@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {
    
    private val viewModel: AlarmRingingViewModel by viewModels()
    
    override fun onStart() {
        super.onStart()
        com.wakey.app.utils.AlarmStateManager.isRingingActivityVisible = true
    }
    
    override fun onStop() {
        super.onStop()
        com.wakey.app.utils.AlarmStateManager.isRingingActivityVisible = false
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get alarm ID from intent
        val alarmId = intent.getIntExtra("alarmId", -1)
        
        // Make activity full-screen and show on lock screen
        setupFullScreen()
        
        setContent {
            // Provide alarmId to ViewModel via composition local or state
            WakeyTheme {
                AlarmRingingScreen(
                    viewModel = viewModel,
                    alarmId = alarmId,
                    onDismiss = ::dismissAlarm,
                    onSnooze = ::snoozeAlarm
                )
            }
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        
        // Pass new ID to ViewModel if valid
        val newId = intent.getIntExtra("alarmId", -1)
        if (newId != -1) {
             // ViewModel.init checks internally if it's the same ID, 
             // so this is safe to call repeatedly.
            viewModel.init(newId)
        }
    }

    /**
     * Setup full-screen display and lock screen flags
     */
    private fun setupFullScreen() {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }
    
    /**
     * Dismiss alarm (only allowed when task is complete)
     */
    private fun dismissAlarm() {
        viewModel.rescheduleIfNeeded()
        AlarmService.stopAlarm(this)
        finish()
    }
    
    /**
     * Snooze alarm for 5 minutes
     */
    private fun snoozeAlarm() {
        viewModel.snoozeAlarm()
        AlarmService.stopAlarm(this)
        finish()
    }
    
    /**
     * Disable back button - user must complete task
     */
    override fun onBackPressed() {
        // Do nothing - user must complete task or snooze
    }
}

/**
 * Alarm Ringing Screen UI.
 * Shows the alarm time, task progress, and action buttons.
 * Designed to match the mockups with gradient progress ring.
 */
@Composable
fun AlarmRingingScreen(
    viewModel: AlarmRingingViewModel,
    alarmId: Int,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    // Initialize ViewModel with alarmId
    LaunchedEffect(alarmId) {
        viewModel.init(alarmId)
    }
    
    val uiState by viewModel.uiState.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryCoral
            )
        } else if (uiState.alarm != null) {
            // Start task execution
            TaskRunner(
                taskType = uiState.alarm!!.taskType,
                viewModel = viewModel
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ============================================
                // MISSION HEADER - Pencil: padding [16, 24], gap 8
                // ============================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Mission Badge
                    AlarmBadge()
                    
                    // Alarm Time - Pencil: alarm icon 16x16 + time text
                    AlarmTimeRow(
                        hour = uiState.alarm!!.hour,
                        minute = uiState.alarm!!.minute
                    )
                }
                
                // ============================================
                // PROGRESS SECTION - Pencil: padding 24, gap 32, fill_container, center
                // ============================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Gradient Progress Ring
                    GradientProgressRing(
                        progress = uiState.taskProgress,
                        taskType = uiState.alarm!!.taskType,
                        stepCount = uiState.stepCount,
                        timeRemaining = uiState.timeRemaining,
                        isVertical = uiState.isDeviceVertical
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Message Section - Pencil: gap 12, center
                    MessageSection(
                        taskType = uiState.alarm!!.taskType,
                        stepCount = uiState.stepCount,
                        timeRemaining = uiState.timeRemaining
                    )
                }
                
                // ============================================
                // BUTTON SECTION - Pencil: padding [16, 24, 34, 24], gap 20
                // ============================================
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 34.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    ActionButtons(
                        isTaskComplete = uiState.isTaskComplete,
                        onDismiss = onDismiss,
                        onSnooze = onSnooze
                    )
                }
            }
        } else {
            // Error state
            Text(
                text = uiState.errorMessage ?: "Unknown error",
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Mission badge at top of screen - Pencil: cornerRadius 20, fill bg-card, gap 8, padding [6, 16]
 */
@Composable
private fun AlarmBadge() {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardBackground,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Pencil: alarm icon 14x14, accent-peach
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AccentOrange,
                modifier = Modifier.size(14.dp)
            )
            // Pencil: Inter 10px weight 600, letter-spacing 2
            Text(
                text = "MORNING MISSION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                ),
                color = TextSecondary
            )
        }
    }
}

/**
 * Alarm Time Row - Pencil: alarm icon 16x16 + time text (Inter 14px)
 */
@Composable
private fun AlarmTimeRow(
    hour: Int,
    minute: Int
) {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Pencil: alarm icon 16x16, accent-peach
        Icon(
            imageVector = Icons.Default.Warning, // Using Warning as placeholder for alarm icon
            contentDescription = null,
            tint = AccentOrange,
            modifier = Modifier.size(16.dp)
        )
        // Pencil: Inter 14px normal, text-primary
        Text(
            text = String.format("%02d:%02d %s", displayHour, minute, period),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp
            ),
            color = TextPrimary
        )
    }
}

/**
 * Message Section - Pencil: gap 12, center aligned
 * "Keep Moving" title + dynamic description
 */
@Composable
private fun MessageSection(
    taskType: TaskType,
    stepCount: Int,
    timeRemaining: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title - Pencil: Montserrat 24px weight 700
        val title = when (taskType) {
            TaskType.STEPS -> "Keep Moving"
            TaskType.HOLD_VERTICAL -> "Hold Steady"
            TaskType.TIME_DELAY -> "Almost There"
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary
        )
        
        // Description - Pencil: Inter 15px normal, text-secondary, center
        val remaining = when (taskType) {
            TaskType.STEPS -> 30 - stepCount
            TaskType.HOLD_VERTICAL, TaskType.TIME_DELAY -> timeRemaining
        }
        
        val descLine1 = when (taskType) {
            TaskType.STEPS -> "Walk $remaining more steps to complete"
            TaskType.HOLD_VERTICAL -> "Hold for $remaining more seconds to complete"
            TaskType.TIME_DELAY -> "Wait $remaining more seconds to complete"
        }
        
        Text(
            text = descLine1,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp
            ),
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "your mission and wake up.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp
            ),
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Gradient circular progress ring - Pencil: 260x260, stroke 18
 * Angular gradient: peach → gold → transparent
 */
@Composable
private fun GradientProgressRing(
    progress: Float,
    taskType: TaskType,
    stepCount: Int,
    timeRemaining: Int,
    isVertical: Boolean
) {
    val ringSize = 260.dp
    val strokeWidth = 18.dp
    
    Box(
        modifier = Modifier.size(ringSize),
        contentAlignment = Alignment.Center
    ) {
        // Background track - Pencil: stroke fill bg-card
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = Offset(stroke / 2, stroke / 2)
            
            drawArc(
                color = CardBackground,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = stroke)
            )
        }
        
        // Gradient progress arc - Pencil: angular gradient peach → gold → transparent
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = strokeWidth.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = Offset(stroke / 2, stroke / 2)
            
            val sweepAngle = progress * 360f
            
            drawArc(
                brush = Brush.sweepGradient(
                    0f to AccentOrange,
                    0.4f to AccentGold,
                    0.41f to Color.Transparent,
                    1f to Color.Transparent
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = Size(diameter, diameter),
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        
        // Center content - Pencil: vertical layout, gap 8
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Step icon container - Pencil: 48x48, cornerRadius 24, fill bg-card
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(CardBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsWalk,
                    contentDescription = null,
                    tint = AccentOrange,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Step count - Pencil: Montserrat 72px weight 700, lineHeight 0.9
            val mainText = when (taskType) {
                TaskType.STEPS -> "$stepCount"
                TaskType.HOLD_VERTICAL, TaskType.TIME_DELAY -> "$timeRemaining"
            }
            
            Text(
                text = mainText,
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 65.sp  // 0.9 * 72
                ),
                color = TextPrimary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Step total - Pencil: Inter 18px weight 600
            val labelText = when (taskType) {
                TaskType.STEPS -> "/ 30 steps"
                TaskType.HOLD_VERTICAL -> "seconds left"
                TaskType.TIME_DELAY -> "seconds left"
            }
            
            Text(
                text = labelText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextSecondary
            )
        }
    }
}

/**
 * Button Section - Pencil: padding [16, 24, 34, 24], gap 20
 * Dismiss button (disabled at 0.4 opacity) + Emergency stop (0.5 opacity)
 */
@Composable
private fun ActionButtons(
    isTaskComplete: Boolean,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    // Emergency stop requires 5 consecutive taps within 2 seconds
    var emergencyTapCount by remember { mutableStateOf(0) }
    var lastTapTime by remember { mutableStateOf(0L) }
    val requiredTaps = 5
    val tapTimeoutMs = 2000L // Reset after 2 seconds of no taps
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Dismiss button - Pencil: cornerRadius 28, height 56, gap 10
        // Disabled style: opacity 0.4, fill text-secondary
        Button(
            onClick = onDismiss,
            enabled = isTaskComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isTaskComplete) PrimaryCoral else TextSecondary,
                disabledContainerColor = TextSecondary,
                contentColor = Color.White,
                disabledContentColor = Color.White
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.alpha(if (isTaskComplete) 1f else 0.4f)
            ) {
                // Pencil: alarm_off icon 22x22
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    modifier = Modifier.size(22.dp)
                )
                // Pencil: Inter 16px weight 600
                Text(
                    text = "Dismiss",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
        
        // Emergency stop - Requires 5 consecutive taps to force dismiss
        // Pencil: gap 8, opacity 0.5
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .alpha(0.5f)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    val currentTime = System.currentTimeMillis()
                    
                    // Reset count if too much time has passed
                    if (currentTime - lastTapTime > tapTimeoutMs) {
                        emergencyTapCount = 0
                    }
                    
                    lastTapTime = currentTime
                    emergencyTapCount++
                    
                    // Force dismiss after 5 taps
                    if (emergencyTapCount >= requiredTaps) {
                        emergencyTapCount = 0
                        onDismiss()
                    }
                }
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            // Pencil: emergency_share icon 14x14
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(14.dp)
            )
            // Pencil: Inter 11px weight 600, letter-spacing 1
            // Show remaining taps if user has started tapping
            val displayText = if (emergencyTapCount > 0) {
                "EMERGENCY STOP (${requiredTaps - emergencyTapCount})"
            } else {
                "EMERGENCY STOP"
            }
            Text(
                text = displayText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                ),
                color = TextSecondary
            )
        }
    }
}

/**
 * Extension to apply alpha to a Row
 */
@Composable
private fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer { this.alpha = alpha }
)


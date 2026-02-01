package com.wakey.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakey.app.ui.theme.CardBackground
import com.wakey.app.ui.theme.PrimaryCoral
import com.wakey.app.ui.theme.TextPrimary
import com.wakey.app.ui.theme.TextSecondary
import com.wakey.app.ui.theme.OutlineLight
import com.wakey.app.ui.theme.SurfaceLight

/**
 * Alarm card component matching HTML design.
 * Shows time, schedule, task info, and toggle switch.
 * 
 * @param time Time string (e.g., "06:30")
 * @param period AM/PM indicator
 * @param schedule Schedule description (e.g., "Mon, Tue, Wed, Thu, Fri")
 * @param taskIcon Icon for the task type
 * @param taskDescription Task description (e.g., "30 Steps")
 * @param isActive Whether alarm is active
 * @param onToggle Callback when toggle is clicked
 * @param onClick Optional callback when card is clicked
 * @param modifier Optional modifier
 */
@Composable
fun AlarmCard(
    time: String,
    period: String,
    schedule: String,
    taskIcon: ImageVector,
    taskDescription: String,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.5f,
        animationSpec = tween(durationMillis = 300),
        label = "card_alpha"
    )
    
    // Pencil Design: cornerRadius 24 ($--radius-lg), fill $--bg-card, padding 20
    Card(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier
            .fillMaxWidth()
            .alpha(alpha),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Time and info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Time display - Pencil: Montserrat 40px weight 300, lineHeight 0.9
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Light,
                            lineHeight = 36.sp  // 0.9 * 40
                        ),
                        color = if (isActive) TextPrimary else TextSecondary
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    // Period - Pencil: Inter 16px weight 500
                    Text(
                        text = period,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Schedule - Pencil: Inter 13px weight 500, color accent-coral
                Text(
                    text = schedule,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = if (isActive) PrimaryCoral else TextSecondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Task badge - Pencil: cornerRadius 20, fill #E4E4E7, padding [6, 10], gap 6
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE4E4E7))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    // taskIcon - Pencil: 14x14
                    Icon(
                        imageVector = taskIcon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = TextSecondary
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    // taskLabel - Pencil: Inter 11px weight 500
                    Text(
                        text = taskDescription,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = TextSecondary
                    )
                }
            }
            
            // Right side: Toggle switch - Pencil: 52x32
            WakeUpSwitch(
                checked = isActive,
                onCheckedChange = onToggle,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/**
 * Helper function to get task icon based on task type
 */
fun getTaskIcon(taskType: String): ImageVector {
    return when (taskType.lowercase()) {
        "steps", "walk" -> Icons.Rounded.DirectionsWalk
        "photo", "camera" -> Icons.Rounded.CameraAlt
        "math", "calculate" -> Icons.Rounded.Calculate
        "voice", "speak", "mic" -> Icons.Rounded.Mic
        else -> Icons.Rounded.DirectionsWalk
    }
}

package com.wakey.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.wakey.app.ui.theme.AccentOrange
import com.wakey.app.ui.theme.PrimaryCoral
import com.wakey.app.ui.theme.TextSecondary

/**
 * Custom Shape for the Bottom Bar with a curved cutout for the FAB
 */
class CurvedCutoutShape(private val fabDiameter: Float, private val fabMargin: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val cutoutRadius = (fabDiameter / 2) + fabMargin
        val cutoutDiameter = cutoutRadius * 2
        val width = size.width
        val height = size.height
        
        val cutoutCenterX = width / 2
        
        // Start from top-left
        path.moveTo(0f, 0f)
        
        // Line to the start of the cutout
        val cutoutStart = cutoutCenterX - cutoutRadius
        path.lineTo(cutoutStart, 0f)
        
        // Draw the cutout (cubic bezier for smooth curve)
        // Control points for a smooth dip
        path.cubicTo(
            cutoutCenterX - cutoutRadius, 0f,
            cutoutCenterX - cutoutRadius, cutoutRadius,
            cutoutCenterX, cutoutRadius
        )
        path.cubicTo(
            cutoutCenterX + cutoutRadius, cutoutRadius,
            cutoutCenterX + cutoutRadius, 0f,
            cutoutCenterX + cutoutRadius, 0f
        )
        
        // Line to top-right
        path.lineTo(width, 0f)
        
        // Complete the rectangle
        path.lineTo(width, height)
        path.lineTo(0f, height)
        path.close()
        
        return Outline.Generic(path)
    }
}

@Composable
fun AlarmListBottomBar(
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    canAddAlarm: Boolean = true,
    modifier: Modifier = Modifier
) {
    val iconColor = TextSecondary
    val fabSize = 56.dp
    val backgroundColor = MaterialTheme.colorScheme.background
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        // Bottom Nav Row - matching Pencil design exactly
        // Height: 90dp with padding [12, 24, 34, 24] -> content area is 90 - 12 - 34 = 44dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Icon - Schedule (current page indicator)
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "Alarms",
                tint = PrimaryCoral,
                modifier = Modifier.size(28.dp)
            )
            
            // Center - Gradient FAB (inline, not floating)
            Box(
                modifier = Modifier
                    .size(fabSize)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PrimaryCoral, AccentOrange),
                            start = Offset(0f, 0f),
                            end = Offset(fabSize.value, fabSize.value)
                        )
                    )
                    .clickable(
                        enabled = canAddAlarm,
                        onClick = onAddClick,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Alarm",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Right Icon - Settings
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = iconColor,
                modifier = Modifier
                    .size(28.dp)
                    .clickable(onClick = onSettingsClick)
            )
        }
        
        // Bottom padding (34dp from design) + navigation bar padding
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(backgroundColor)
        )
        
        // Navigation bar padding for phones with button navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .navigationBarsPadding()
        )
    }
}

/**
 * Bottom App Bar for Set Alarm screen
 * Full-width gradient confirm button matching Pencil design
 */
@Composable
fun SetAlarmBottomBar(
    onSetClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
    ) {
        // Confirm Button - Pencil: full width, height 56, cornerRadius 28
        // Gradient: gold → peach (linear, rotation 90)
        // Padding: [16, 24, 34, 24]
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF6C57D), // accent-gold
                            Color(0xFFED9D6F)  // accent-peach
                        )
                    )
                )
                .clickable(
                    enabled = !isLoading,
                    onClick = onSetClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Check icon - Pencil: 22x22
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Confirm text - Pencil: Inter 16px 600
                    androidx.compose.material3.Text(
                        text = "Confirm",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
            }
        }
        
        // Bottom padding (34dp from design)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .background(backgroundColor)
        )
        
        // Navigation bar padding for phones with button navigation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .navigationBarsPadding()
        )
    }
}

package com.wakey.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wakey.app.ui.theme.SwitchOffColor
import com.wakey.app.ui.theme.SwitchOnColor

/**
 * Custom toggle switch component matching the HTML design.
 * Features smooth animation and custom colors.
 * 
 * @param checked Current state of the switch
 * @param onCheckedChange Callback when state changes
 * @param modifier Optional modifier
 * @param enabled Whether the switch is enabled
 * @param thumbSize Size of the circular thumb
 * @param trackHeight Height of the track
 * @param trackWidth Width of the track
 */
@Composable
fun WakeUpSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    thumbSize: Dp = 26.dp,
    trackHeight: Dp = 32.dp,
    trackWidth: Dp = 52.dp
) {
    val thumbPadding = 3.dp
    val thumbOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - thumbPadding else thumbPadding,
        animationSpec = tween(durationMillis = 300),
        label = "thumb_offset"
    )
    
    val trackColor = if (checked) SwitchOnColor else SwitchOffColor
    
    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.5f,
        animationSpec = tween(durationMillis = 200),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .width(trackWidth)
            .height(trackHeight)
            .clip(RoundedCornerShape(trackHeight / 2))
            .background(trackColor.copy(alpha = alpha))
            .clickable(
                enabled = enabled,
                onClick = { onCheckedChange(!checked) },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb (circular button) with shadow effect
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

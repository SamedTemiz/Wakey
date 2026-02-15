package com.wakey.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wakey.app.ui.theme.PrimaryCoral
import kotlin.math.absoluteValue

/**
 * A custom roller-style time picker component that uses VerticalPager for infinite looping.
 * Provides a premium look with larger numbers and smooth transitions.
 */
@Composable
fun RollerTimePicker(
    initialHour: Int,
    initialMinute: Int,
    onTimeChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val hours = (0..23).toList()
    val minutes = (0..59).toList()
    
    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }
    
    // Notify parent
    LaunchedEffect(selectedHour, selectedMinute) {
        onTimeChange(selectedHour, selectedMinute)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Selection highlight background - Fixed height to match items
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(PrimaryCoral.copy(alpha = 0.1f))
                .border(BorderStroke(1.dp, PrimaryCoral), RoundedCornerShape(12.dp))
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            // Hours Column
            TimeColumn(
                items = hours,
                initialValue = initialHour,
                onValueChange = { selectedHour = it }
            )
            
            // Separator
            Text(
                text = ":",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            
            // Minutes Column
            TimeColumn(
                items = minutes,
                initialValue = initialMinute,
                onValueChange = { selectedMinute = it }
            )
        }
    }
}

@Composable
private fun RowScope.TimeColumn(
    items: List<Int>,
    initialValue: Int,
    onValueChange: (Int) -> Unit
) {
    // For infinite looping we use a large page count and modulo
    val pageCount = 10000
    val initialPage = (pageCount / 2 / items.size) * items.size + initialValue
    val pagerState = rememberPagerState(initialPage = initialPage) { pageCount }
    
    // Sync external state
    LaunchedEffect(pagerState.currentPage) {
        onValueChange(items[pagerState.currentPage % items.size])
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier
            .weight(1f)
            .height(200.dp),
        contentPadding = PaddingValues(vertical = 76.dp), // 200/2 - 48/2
        horizontalAlignment = Alignment.CenterHorizontally
    ) { page ->
        val itemValue = items[page % items.size]
        val pageOffset = (
            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        ).absoluteValue

        // Visual effects for the wheel look - Subtler rotation to avoid crowding
        val alpha = (1f - (pageOffset * 0.45f)).coerceIn(0.1f, 1f)
        val scale = (1f - (pageOffset * 0.12f)).coerceIn(0.7f, 1f)
        val rotationX = (-35f * pageOffset).coerceIn(-45f, 45f) * (if (page < pagerState.currentPage) -1 else 1)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .graphicsLayer {
                    this.alpha = alpha
                    this.scaleX = scale
                    this.scaleY = scale
                    this.rotationX = rotationX
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = itemValue.toString().padStart(2, '0'),
                style = TextStyle(
                    fontSize = 26.sp,
                    fontWeight = if (pageOffset < 0.5f) FontWeight.SemiBold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

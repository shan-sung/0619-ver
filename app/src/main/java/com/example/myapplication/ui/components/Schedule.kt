package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.ScheduleItem
import com.example.myapplication.data.ScheduleTime
import com.example.myapplication.ui.theme.AppTheme
import java.time.format.DateTimeFormatter

object LineParametersDefaults {

    private val defaultStrokeWidth = 3.dp

    fun linearGradient(
        strokeWidth: Dp = defaultStrokeWidth,
        startColor: Color,
        endColor: Color,
        startY: Float = 0.0f,
        endY: Float = Float.POSITIVE_INFINITY
    ): LineParameters {
        val brush = Brush.verticalGradient(
            colors = listOf(startColor, endColor),
            startY = startY,
            endY = endY
        )
        return LineParameters(strokeWidth, brush)
    }
}

enum class TimelineNodePosition {
    FIRST,
    MIDDLE,
    LAST
}

data class CircleParameters(
    val radius: Dp,
    val backgroundColor: Color
)

object CircleParametersDefaults {
    private val defaultRadius = 6.dp

    fun circleParameters(backgroundColor: Color): CircleParameters {
        return CircleParameters(radius = defaultRadius, backgroundColor = backgroundColor)
    }
}

data class LineParameters(
    val strokeWidth: Dp,
    val brush: Brush
)

@Composable
fun Timeline(schedule: List<ScheduleItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val sortedSchedule = remember(schedule) {
            schedule.sortedBy { it.startTime }
        }

        sortedSchedule.forEachIndexed { index, item ->
            val position = when (index) {
                0 -> TimelineNodePosition.FIRST
                sortedSchedule.lastIndex -> TimelineNodePosition.LAST
                else -> TimelineNodePosition.MIDDLE
            }

            TimelineNode(
                position = position,
                circleParameters = CircleParametersDefaults.circleParameters(
                    backgroundColor = MaterialTheme.colorScheme.primary
                ),
                lineParameters = if (position != TimelineNodePosition.LAST)
                    LineParametersDefaults.linearGradient(
                        startColor = MaterialTheme.colorScheme.primary,
                        endColor = MaterialTheme.colorScheme.primary
                    )
                else null,
                contentStartOffset = 40.dp
            ) { modifier ->
                MessageBubble(item = item)
            }
        }

    }
}

@Composable
fun TimelineNode(
    position: TimelineNodePosition,
    circleParameters: CircleParameters,
    lineParameters: LineParameters? = null, // 可以選擇不畫線
    contentStartOffset: Dp = 40.dp,
    spacerBetweenNodes: Dp = 32.dp,
    content: @Composable BoxScope.(modifier: Modifier) -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .drawBehind {
                val circleRadiusInPx = circleParameters.radius.toPx()
                drawCircle(
                    color = circleParameters.backgroundColor,
                    radius = circleRadiusInPx,
                    center = Offset(circleRadiusInPx, circleRadiusInPx)
                )
                lineParameters?.let {
                    drawLine(
                        brush = it.brush,
                        start = Offset(x = circleRadiusInPx, y = circleRadiusInPx * 2),
                        end = Offset(x = circleRadiusInPx, y = this.size.height),
                        strokeWidth = it.strokeWidth.toPx()
                    )
                }
            }
            .padding(bottom = 16.dp)
    ) {
        content(
            Modifier
                .padding(
                    start = contentStartOffset,
                    bottom = if (position != TimelineNodePosition.LAST) {
                        spacerBetweenNodes
                    } else {
                        0.dp
                    }
                )
        )
    }
}

@Composable
fun MessageBubble(modifier: Modifier = Modifier, item: ScheduleItem) {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a") // 例如 8:00 AM

    val timeText = item.startTime?.let { start ->
        item.endTime?.let { end ->
            "${start.format(timeFormatter)} – ${end.format(timeFormatter)}"
        }
    } ?: item.startTime?.let { start ->
        "${start.format(timeFormatter)} – 未定"
    } ?: item.endTime?.let { end ->
        "未定 – ${end.format(timeFormatter)}"
    } ?: "時間未定"


    Column(modifier = modifier.padding(bottom = 10.dp, start = 40.dp)) {
        Text(
            text = item.activity,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        )
        Spacer(modifier = Modifier.height(1.dp))
        Text(
            text = timeText,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimelinePreview() {
    val schedule = listOf(
        ScheduleItem(
            day = 1,
            time = ScheduleTime(start = "19:00", end = "20:00"),
            activity = "Evening: Dinner & Drinks",
            transportation = "Boat"
        )
    )

    AppTheme {
        Timeline(schedule = schedule)
    }
}
package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.ScheduleItem
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.items

@Composable
fun ScheduleList(
    schedule: List<ScheduleItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        val sortedSchedule = schedule.sortedBy { it.startTime }

        items(sortedSchedule) { item ->
            MessageBubble(item = item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun MessageBubble(
    modifier: Modifier = Modifier,
    item: ScheduleItem,
    onClick: () -> Unit = {}
) {
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
    val timeText = item.startTime?.let { start ->
        item.endTime?.let { end ->
            "${start.format(timeFormatter)} – ${end.format(timeFormatter)}"
        }
    } ?: item.startTime?.let { start ->
        "${start.format(timeFormatter)} – 未定"
    } ?: item.endTime?.let { end ->
        "未定 – ${end.format(timeFormatter)}"
    } ?: "時間未定"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 圓形底色 icon
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = Color(0xFFE0F7FA), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "Event Icon",
                tint = Color(0xFF00796B),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.activity,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = timeText,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

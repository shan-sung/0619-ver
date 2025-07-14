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
import androidx.compose.foundation.lazy.items
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

@Composable
fun ScheduleTimeline(
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
            ScheduleItemCard(item = item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ScheduleItemCard(
    item: ScheduleItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 圓形底色 icon         .background(MaterialTheme.colorScheme.surface), // 使用主題的 surface 顏色
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "Event Icon",
                tint = MaterialTheme.colorScheme.primary,
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
                text = item.formatTimeRange(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

fun ScheduleItem.formatTimeRange(): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")

    val start = this.startTime
    val end = this.endTime

    return when {
        start != null && end != null -> "${start.format(formatter)} – ${end.format(formatter)}"
        start != null -> "${start.format(formatter)} – 未定"
        end != null -> "未定 – ${end.format(formatter)}"
        else -> "時間未定"
    }
}
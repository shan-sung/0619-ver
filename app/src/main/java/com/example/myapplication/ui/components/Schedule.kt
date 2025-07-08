package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.ScheduleItem

@Composable
fun ScheduleList(schedule: List<ScheduleItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 16.dp)
    ) {
        schedule.forEachIndexed { index, item ->
            ScheduleItemTimeline(
                item = item,
                isLastItem = index == schedule.lastIndex
            )
        }
    }
}

@Composable
fun ScheduleItemTimeline(item: ScheduleItem, isLastItem: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // 左側：圓點與垂直虛線
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Canvas(modifier = Modifier.size(12.dp)) {
                drawCircle(
                    color = Color.Black,
                    radius = size.minDimension / 2,
                    style = Fill // 明確指定填滿
                )
            }

            if (!isLastItem) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(Color.LightGray)
                )
            }
        }

        // 右側：行程內容
        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = item.activity,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.time,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF607D8B) // 藍灰色
            )
        }
    }
}
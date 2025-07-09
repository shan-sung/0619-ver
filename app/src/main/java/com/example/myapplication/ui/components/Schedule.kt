package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
            .padding(horizontal = 16.dp)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        // 左側：圓點與垂直線（寬度固定）
        Box(
            modifier = Modifier
                .width(24.dp), // ✅ 固定寬度，讓右側對齊
            contentAlignment = Alignment.TopCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(
                        color = Color.Black,
                        radius = size.minDimension / 2,
                        style = Fill
                    )
                }

                if (!isLastItem) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(48.dp) // 可微調
                            .background(Color.LightGray)
                    )
                }
            }
        }

        // 右側：文字區塊
        Column {
            Text(
                text = item.activity,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.time,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF607D8B)
            )
        }
    }
}

package com.example.myapplication.ui.screens.myplans.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun OpeningHoursSection(
    hours: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val now = remember { LocalTime.now() }
    val today = remember { LocalDate.now().dayOfWeek.name } // e.g., MONDAY

    fun String.toChineseDay(): String = when (this.uppercase()) {
        "MONDAY" -> "星期一"
        "TUESDAY" -> "星期二"
        "WEDNESDAY" -> "星期三"
        "THURSDAY" -> "星期四"
        "FRIDAY" -> "星期五"
        "SATURDAY" -> "星期六"
        "SUNDAY" -> "星期日"
        else -> this
    }

    fun parseTimeString(input: String): LocalTime? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("h[[:]mm] a", Locale.ENGLISH)
            LocalTime.parse(input.uppercase(Locale.ENGLISH), formatter)
        } catch (e: Exception) {
            null
        }
    }

    // 取得今天營業時間（如 10 AM–5 PM）
    val todayHours = hours.find { it.startsWith(today, ignoreCase = true) }
    val timeRange = todayHours?.split(":", limit = 2)?.getOrNull(1)?.trim()

    val (statusText, statusColor) = remember(timeRange) {
        try {
            if (timeRange == null || timeRange.contains("Closed", ignoreCase = true)) {
                "已打烊" to Color.Red
            } else if (!timeRange.contains("–")) {
                "營業資訊錯誤" to Color.Gray
            } else {
                val parts = timeRange.split("–").map { it.trim() }
                if (parts.size != 2) return@remember "營業資訊錯誤" to Color.Gray

                val openTime = parseTimeString(parts[0])
                val closeTime = parseTimeString(parts[1])

                if (openTime != null && closeTime != null) {
                    if (now.isAfter(openTime) && now.isBefore(closeTime)) {
                        val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.CHINESE)
                        val closeDisplay = closeTime.format(formatter).replace("AM", "上午").replace("PM", "下午")
                        "營業中 · 至 $closeDisplay" to Color(0xFF007B8F)
                    } else {
                        val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.CHINESE)
                        val openDisplay = openTime.format(formatter).replace("AM", "上午").replace("PM", "下午")
                        "尚未營業 · $openDisplay 開始" to Color.Red
                    }
                } else {
                    "營業資訊錯誤" to Color.Gray
                }
            }
        } catch (e: Exception) {
            "營業資訊錯誤" to Color.Gray
        }
    }


    Column(modifier = modifier) {
        // ⏰ 顯示狀態
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = Color(0xFF007B8F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = statusText,
                color = statusColor,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        // 📅 營業時間表
        if (expanded) {
            Spacer(Modifier.height(4.dp))
            Column {
                hours.forEach { hour ->
                    val (dayEn, time) = hour.split(":", limit = 2).map { it.trim() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dayEn.toChineseDay(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = time,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

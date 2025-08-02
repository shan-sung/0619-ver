package com.example.myapplication.ui.components.placedetaildialog.comp

import android.util.Log
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


    // 擷取今天營業時間（例如：MONDAY: 10 AM–5 PM）
    val todayHours = hours.find { it.uppercase().startsWith(today) }
    val timeRange = todayHours
        ?.split(":", limit = 2)
        ?.getOrNull(1)
        ?.let { normalizeTimeRange(it) }

    val statusInfo = runCatching {
        // 這裡是原本 try 裡的邏輯
        if (timeRange.isNullOrBlank()) {
            "今日營業資訊缺漏" to MaterialTheme.colorScheme.onSurfaceVariant
        } else if (timeRange.equals("Closed", ignoreCase = true)) {
            "已打烊" to MaterialTheme.colorScheme.primary
        } else if (timeRange.equals("24 Hours", ignoreCase = true)) {
            "24 小時營業" to MaterialTheme.colorScheme.primary
        } else {
            val parts = timeRange.split("-").map { it.trim() }
            if (parts.size != 2) throw IllegalArgumentException("格式錯誤")
            val openTime = parseTimeStringFlexible(parts[0])
            val closeTime = parseTimeStringFlexible(parts[1])
            if (openTime == null || closeTime == null) throw IllegalArgumentException("時間解析失敗")

            val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.CHINESE)
            if (now.isAfter(openTime) && now.isBefore(closeTime)) {
                val closeDisplay = closeTime.format(formatter)
                    .replace("AM", "上午").replace("PM", "下午")
                "營業中 · 至 $closeDisplay" to MaterialTheme.colorScheme.primary
            } else {
                val openDisplay = openTime.format(formatter)
                    .replace("AM", "上午").replace("PM", "下午")
                "尚未營業 · $openDisplay 開始" to MaterialTheme.colorScheme.primary
            }
        }
    }.getOrElse {
        Log.e("OpeningHours", "營業時間解析失敗：${it.message}")
        "營業資訊錯誤" to MaterialTheme.colorScheme.onSurfaceVariant
    }

    val (statusText, statusColor) = remember(statusInfo) { statusInfo }

    Column(modifier = modifier) {
        // ⏰ 營業狀態
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

        // 📅 每日營業時間表
        if (expanded) {
            Spacer(Modifier.height(4.dp))
            Column {
                hours.forEach { hour ->
                    val (dayEn, time) = try {
                        val parts = hour.split(":", limit = 2).map { it.trim() }
                        if (parts.size != 2) throw IllegalArgumentException("格式錯誤")
                        parts[0] to parts[1]
                    } catch (e: Exception) {
                        "格式錯誤" to hour
                    }

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

fun normalizeTimeRange(raw: String): String {
    return raw
        .replace("–", "-")
        .replace("—", "-")
        .replace(Regex("[\u2000-\u206F\u2E00-\u2E7F\\s]+"), " ") // 移除特殊空白符
        .replace(Regex("\\s*-\\s*"), "-") // 標準化 dash
        .trim()
}

fun parseTimeStringFlexible(input: String): LocalTime? {
    val formats = listOf("h:mm a", "h a", "hh:mm a", "hh a")
    for (pattern in formats) {
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
            return LocalTime.parse(input.uppercase(Locale.ENGLISH), formatter)
        } catch (_: Exception) {
        }
    }
    return null
}

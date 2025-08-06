package com.example.myapplication.ui.components.dialogs.placedetaildialog.comp

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    val today = remember { LocalDate.now().dayOfWeek.name } // e.g. MONDAY
    val colorScheme = MaterialTheme.colorScheme

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

    val status = remember(hours) { getOpeningStatusInfo(hours, now, colorScheme) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        // ⏰ 營業狀態列（點擊可展開）
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
                tint = colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = status.text,
                color = status.color,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp).padding(end = 8.dp)
            )
        }

        // 📅 展開營業時間
        if (expanded) {
            Column(modifier = Modifier.padding(start = 4.dp, top = 4.dp)) {
                hours.forEach { hour ->
                    val (dayEn, time) = try {
                        val parts = hour.split(":", limit = 2).map { it.trim() }
                        if (parts.size != 2) throw IllegalArgumentException("格式錯誤")
                        parts[0] to parts[1]
                    } catch (e: Exception) {
                        "格式錯誤" to hour
                    }

                    val isToday = dayEn.uppercase() == today
                    val textColor = if (isToday) colorScheme.onSurface else colorScheme.onSurfaceVariant

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dayEn.toChineseDay(),
                            color = textColor,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = time,
                            color = textColor,
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
        .replace("–", "-") // EN DASH
        .replace("—", "-") // EM DASH
        .replace("−", "-") // MINUS SIGN
        .replace("–", "-") // DOUBLE ENSURE
        .replace("~", "-")
        .replace(" to ", "-")
        .replace("–", "-")
        .replace(Regex("[\u2000-\u206F\u2E00-\u2E7F\\s]+"), " ")
        .replace(Regex("\\s*-\\s*"), "-")
        .trim()
}

fun parseTimeStringFlexible(input: String): LocalTime? {
    val clean = input.trim()
        .replace(".", "")     // 例：10 a.m. -> 10 am
        .replace("–", "-")    // normalize dash
        .replace("to", "-")   // 10am to 5pm → 10am-5pm

    val formats = listOf("h:mm a", "h a", "hh:mm a", "hh a")

    for (pattern in formats) {
        try {
            val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH)
            return LocalTime.parse(clean.uppercase(Locale.ENGLISH), formatter)
        } catch (_: Exception) {}
    }
    return null
}
data class OpeningStatusInfo(val text: String, val color: Color)

fun getOpeningStatusInfo(
    hours: List<String>,
    now: LocalTime = LocalTime.now(),
    colorScheme: androidx.compose.material3.ColorScheme
): OpeningStatusInfo {
    val today = LocalDate.now().dayOfWeek.name.lowercase() // "monday"
    val todayHours = hours.find {
        it.substringBefore(":").trim().lowercase() == today
    }
    val timeRange = todayHours
        ?.split(":", limit = 2)
        ?.getOrNull(1)
        ?.let { normalizeTimeRange(it) }
    Log.d("OpeningHours", "原始 todayHours = $todayHours")
    Log.d("OpeningHours", "標準化後 timeRange = $timeRange")

    if (timeRange.isNullOrBlank()) {
        return OpeningStatusInfo("今日營業資訊缺漏", colorScheme.onSurfaceVariant)
    }

    if (timeRange.equals("Closed", ignoreCase = true)) {
        return OpeningStatusInfo("已打烊", colorScheme.primary)
    }

    val normalized = timeRange.lowercase()
    if (normalized.contains("24") && normalized.contains("hour")) {
        return OpeningStatusInfo("24 小時營業", colorScheme.primary)
    }

    val parts = timeRange.split("-").map { it.trim() }
    if (parts.size != 2) {
        Log.e("OpeningHours", "時間格式錯誤：$timeRange")
        return OpeningStatusInfo("營業資訊錯誤", colorScheme.onSurfaceVariant)
    }

    val openRaw = parts[0]
    val closeRaw = parts[1]
    val closeTime = parseTimeStringFlexible(closeRaw)
    val openRawFinal = if (!openRaw.contains("AM", true) && !openRaw.contains("PM", true)) {
        val suffix = closeRaw.takeLastWhile { it != ' ' }.uppercase()
        "$openRaw $suffix"
    } else {
        openRaw
    }
    val openTime = parseTimeStringFlexible(openRawFinal)

    if (openTime == null || closeTime == null) {
        Log.e("OpeningHours", "時間解析失敗：$timeRange")
        return OpeningStatusInfo("營業資訊錯誤", colorScheme.onSurfaceVariant)
    }

    val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.CHINESE)
    return if (now.isAfter(openTime) && now.isBefore(closeTime)) {
        val closeDisplay = closeTime.format(formatter).replace("AM", "上午").replace("PM", "下午")
        OpeningStatusInfo("營業中 · 至 $closeDisplay", colorScheme.primary)
    } else {
        val openDisplay = openTime.format(formatter).replace("AM", "上午").replace("PM", "下午")
        OpeningStatusInfo("尚未營業 · $openDisplay 開始", colorScheme.primary)
    }
}
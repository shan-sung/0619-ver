package com.example.myapplication.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

fun formatRelativeTime(isoString: String): String {
    return try {
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        val requestTime = LocalDateTime.parse(isoString, formatter)
        val now = LocalDateTime.now(ZoneOffset.UTC)

        val duration = Duration.between(requestTime, now)

        when {
            duration.seconds < 60 -> "${duration.seconds}s"
            duration.toMinutes() < 60 -> "${duration.toMinutes()}m"
            duration.toHours() < 24 -> "${duration.toHours()}h"
            duration.toDays() < 7 -> "${duration.toDays()}d"
            duration.toDays() < 30 -> "${duration.toDays() / 7}w"
            duration.toDays() < 365 -> "${duration.toDays() / 30}mo"
            else -> "${duration.toDays() / 365}y"
        }
    } catch (e: Exception) {
        "" // 無法解析就不顯示
    }
}

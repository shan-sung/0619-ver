package com.example.myapplication.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

data class TripCreationInfo(
    val userId: String = "",  // 或使用 val userId: String? = null
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val peopleCount: Int = 1,
    val averageAgeRange: String = "",
    val preferences: List<String> = emptyList(),
    val transportOptions: List<String> = emptyList(),
    val cities: List<String> = emptyList(),
    val budget: Int = 0,
    val title: String = ""
)

data class Attraction(
    val id: String,
    val name: String,
    val rating: Double? = null,
    val tags: List<String>? = null,

    // 以下欄位為選填，從 Google Text Search API 無法直接取得
    val city: String = "",                    // 可留空，或後續補上
    val country: String = "",                 // 同上
    val description: String? = null,          // 可從 formatted_address 暫時替代
    val imageUrl: String? = null              // 可透過 Place Details API 拿照片
)


data class Travel(
    val _id: String,
    val userId: String,
    val chatRoomId: String,
    val members: List<String>,
    val created: Boolean = false,
    val title: String?,
    val startDate: String,
    val endDate: String,
    val budget: Int? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val itinerary: List<ItineraryDay>? = null
) {
    val days: Int
        get() {
            return try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val start = LocalDate.parse(startDate, formatter)
                val end = LocalDate.parse(endDate, formatter)
                ChronoUnit.DAYS.between(start, end).toInt() + 1
            } catch (e: Exception) {
                0
            }
        }
}

@Serializable
data class ChatMessage(
    val id: String,
    val chatRoomId: String,             // 對應 Travel.chatRoomId
    val senderId: String,
    val sender: String,                 // 可選（顯示暱稱）
    val message: String,
    val timestamp: Long
)


data class ItineraryDay(
    val day: Int,               // 第幾天
    val schedule: List<ScheduleItem>
)

data class ScheduleItem(
    val day: Int,
    val time: ScheduleTime,
    val activity: String,
    val transportation: String,
    val note: String? = ""
) {
    val startTime: LocalTime?
        get() = time.start.toLocalTimeOrNull()
    val endTime: LocalTime?
        get() = time.end.toLocalTimeOrNull()
}


data class ScheduleTime(
    val start: String,              // "08:00"
    val end: String                 // "09:00"
)

// 擴充函式：讓 String 支援安全轉換為 LocalTime
fun String.toLocalTimeOrNull(): LocalTime? {
    return try {
        LocalTime.parse(this, DateTimeFormatter.ofPattern("H:mm"))
    } catch (e: Exception) {
        println("Time parse failed: $this")
        null
    }
}

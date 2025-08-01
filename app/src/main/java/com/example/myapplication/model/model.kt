package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
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
    val google: Boolean = false,
    val title: String = "",
    val dailyStartHour: Int = 9,  // e.g. 9:00 AM
    val dailyEndHour: Int = 20    // e.g. 8:00 PM
)

@Parcelize
data class Attraction(
    val id: String,
    val name: String,
    val address: String? = null,
    val rating: Double? = null,
    val userRatingsTotal: Int? = null,
    val tags: List<String>? = null,
    val city: String = "",
    val country: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val openingHours: List<String>? = null,   // 🔹新增
    val comments: List<Comment>? = null,       // 🔹新增
    val lat: Double? = null,       // ← 加這行
    val lng: Double? = null
) : Parcelable

@Parcelize
data class Comment(
    val id: String,
    val user: String,
    val rating: Int,
    val text: String
) : Parcelable


@Parcelize
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
) : Parcelable {
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
    val timestamp: String
)

@Parcelize
data class ItineraryDay(
    val day: Int,               // 第幾天
    val schedule: List<ScheduleItem>
): Parcelable

@Parcelize
data class ScheduleItem(
    val day: Int,
    val time: ScheduleTime,
    val transportation: String,
    val note: String? = "",
    val placeId: String? = null,
    val placeName: String,
    val photoReference: String? = null // ✅ 新增：Google Places 的照片參照
) : Parcelable {
    val startTime: LocalTime?
        get() = time.start.toLocalTimeOrNull()
    val endTime: LocalTime?
        get() = time.end.toLocalTimeOrNull()
}

@Parcelize
data class ScheduleTime(
    val start: String,              // "08:00"
    val end: String                 // "09:00"
): Parcelable

// 擴充函式：讓 String 支援安全轉換為 LocalTime
fun String.toLocalTimeOrNull(): LocalTime? {
    return try {
        LocalTime.parse(this, DateTimeFormatter.ofPattern("H:mm"))
    } catch (e: Exception) {
        println("Time parse failed: $this")
        null
    }
}
package com.example.myapplication.data

import java.time.LocalDate

data class TripCreationInfo(
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val peopleCount: Int = 1,
    val averageAgeRange: String = "",         // 例如 "21-25"
    val preferences: List<String> = emptyList(),     // ex: ["美食", "文化"]
    val transportOptions: List<String> = emptyList(), // ex: ["步行", "大眾運輸"]
    val cities: List<String> = emptyList(),   // ex: ["台北", "台中"]
    val budget: Int = 0,
    val title: String = ""
)

data class Attraction(
    val id: String,
    val name: String,
    val city: String,
    val country: String,
    val rating: Double?,
    val imageUrl: String? = null
)

data class TripRequestResponse(
    val _id: String,
    val title: String,
    val startDate: String?,         // 建議用 String 接收後端回傳的日期
    val endDate: String?,
    val peopleCount: Int,
    val averageAgeRange: String,
    val preferences: List<String>,
    val transportOptions: List<String>,
    val cities: List<String>,
    val budget: Int,
    val createdAt: String
)

data class Travel(
    val _id: String,
    val created: Boolean = false,
    val title: String,
    val days: Int? = null,
    val members: Int? = null,
    val budget: Int? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val itinerary: List<ItineraryDay>? = null
)

data class ItineraryDay(
    val day: Int,
    val date: String,
    val schedule: List<ScheduleItem>
)

data class ScheduleItem(
    val time: String,
    val activity: String,
    val transportation: String
)

data class PlacesSearchResponse(
    val results: List<PlaceResult>,
    val status: String
)

data class PlaceResult(
    val place_id: String,
    val name: String,
    val rating: Double?,
    val vicinity: String?,
    val photos: List<Photo>?,
    val geometry: Geometry
)

data class Photo(
    val photo_reference: String
)

data class GeocodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val address_components: List<AddressComponent>,
    val formatted_address: String,
    val geometry: Geometry,
    val place_id: String,
    val types: List<String>
)

data class AddressComponent(
    val long_name: String,
    val short_name: String,
    val types: List<String>
)

data class Geometry(
    val location: LatLng
)

data class LatLng(
    val lat: Double,
    val lng: Double
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val message: String,
    val timestamp: Long
)
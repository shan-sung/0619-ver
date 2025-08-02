package com.example.myapplication.data.model

data class PlaceDetailsResponse(
    val result: PlaceDetailResult
)

data class PlaceDetailResult(
    val name: String?,
    val formatted_address: String?,
    val opening_hours: OpeningHours?,
    val rating: Double?,
    val user_ratings_total: Int?,
    val reviews: List<Review>?,
    val photos: List<Photo>?,
    val geometry: Geometry? // 🔹 加這行
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)

data class Photo(
    val photo_reference: String
)

data class OpeningHours(
    val weekday_text: List<String>
)

data class Review(
    val author_name: String,
    val rating: Int,
    val text: String
)

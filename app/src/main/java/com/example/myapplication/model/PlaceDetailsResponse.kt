package com.example.myapplication.model

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
    val photos: List<Photo>?
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

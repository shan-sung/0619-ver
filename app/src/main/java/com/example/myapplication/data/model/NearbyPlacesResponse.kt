package com.example.myapplication.data.model

import com.example.myapplication.BuildConfig

data class NearbyPlacesResponse(
    val results: List<PlaceResult>
)

data class PlaceResult(
    val place_id: String,
    val name: String,
    val rating: Double?,
    val types: List<String>?,
    val vicinity: String?,
    val photos: List<Photo>?
)

fun buildPhotoUrl(photoRef: String): String {
    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoRef&key=${BuildConfig.MAPS_API_KEY}"
}

data class TextSearchResponse(
    val results: List<TextSearchPlace>,
    val status: String
)

data class TextSearchPlace(
    val name: String,
    val place_id: String,
    val formatted_address: String,
    val rating: Double?,
    val user_ratings_total: Int?,
    val types: List<String>?,
    val photos: List<Photo>?
)

fun TextSearchPlace.toAttraction(): Attraction {
    return Attraction(
        id = place_id,
        name = name,
        address = formatted_address,
        rating = rating ?: 0.0,
        tags = types.orEmpty(),
        description = formatted_address,
        imageUrl = photos?.firstOrNull()?.photo_reference?.let { buildPhotoUrl(it) }
    )
}
package com.example.myapplication.data.model

import com.example.myapplication.BuildConfig

fun PlaceDetailResult.toAttraction(placeId: String): Attraction {
    return Attraction(
        id = placeId,
        name = name.orEmpty(),
        address = formatted_address,
        rating = rating,
        userRatingsTotal = user_ratings_total,
        imageUrl = photos?.firstOrNull()?.photo_reference?.let {
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=$it&key=${BuildConfig.MAPS_API_KEY}"
        },
        openingHours = opening_hours?.weekday_text,
        comments = reviews?.mapIndexed { index, review ->
            Comment(
                id = "$placeId-comment-$index",
                user = review.author_name,
                rating = review.rating,
                text = review.text
            )
        },
        lat = geometry?.location?.lat,
        lng = geometry?.location?.lng
    )
}

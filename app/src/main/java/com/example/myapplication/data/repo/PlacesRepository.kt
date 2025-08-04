package com.example.myapplication.data.repo

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.api.PlacesApiService
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.Comment
import com.example.myapplication.data.model.NearbyPlacesResponse
import java.util.UUID
import javax.inject.Inject

class PlacesRepository @Inject constructor(
    private val api: PlacesApiService
) {
    suspend fun getNearbyAttractionsAndRestaurants(location: String): NearbyPlacesResponse {
        val attractions = api.getNearbyPlaces(location, type = "tourist_attraction").results
        val restaurants = api.getNearbyPlaces(location, type = "restaurant").results
        return NearbyPlacesResponse(
            results = (attractions + restaurants).distinctBy { it.place_id }
        )
    }
    suspend fun getPlaceDetails(placeId: String): Attraction {
        val detail = api.getPlaceDetails(placeId).result
        val photoReference = detail.photos?.firstOrNull()?.photo_reference
        val imageUrl = photoReference?.let {
            "https://maps.googleapis.com/maps/api/place/photo" +
                    "?maxwidth=800&photoreference=$it&key=${BuildConfig.MAPS_API_KEY}"
        }

        return Attraction(
            id = placeId,
            name = detail.name ?: "",
            address = detail.formatted_address,
            rating = detail.rating,
            userRatingsTotal = detail.user_ratings_total,
            openingHours = detail.opening_hours?.weekday_text,
            comments = detail.reviews?.map {
                Comment(
                    id = UUID.randomUUID().toString(),
                    user = it.author_name,
                    rating = it.rating,
                    text = it.text
                )
            },
            imageUrl = imageUrl
        )
    }
}
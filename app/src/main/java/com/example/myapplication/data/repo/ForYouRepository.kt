package com.example.myapplication.data.repo

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.api.ForYouApiService
import com.example.myapplication.data.api.PlacesApiService
import com.example.myapplication.data.api.SavedApiService
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.Comment
import jakarta.inject.Inject
import java.util.UUID

class ForYouRepository @Inject constructor(
    private val forYouApi: ForYouApiService,
    private val placesApi: PlacesApiService,
    private val savedApi: SavedApiService  // ⬅️ 新增
) {
    suspend fun getRecommendations(userId: String): List<Attraction> =
        forYouApi.getRecommendations(userId)

    suspend fun getPlaceDetails(placeId: String): Attraction {
        val detail = placesApi.getPlaceDetails(placeId).result
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

    suspend fun addToSaved(userId: String, attraction: Attraction) =
        savedApi.addToSaved(userId, attraction) // ⬅️ 加入最愛
}

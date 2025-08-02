package com.example.myapplication.data.repo

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.api.PlacesApiService
import com.example.myapplication.data.api.SavedApiService
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.Comment
import com.example.myapplication.data.model.TextSearchPlace
import java.util.UUID
import javax.inject.Inject

class SavedRepository @Inject constructor(
    private val savedApi: SavedApiService,
    private val placesApi: PlacesApiService
) {
    suspend fun getSavedAttractions(userId: String): List<Attraction> =
        savedApi.getSavedAttractions(userId)

    suspend fun addToSaved(userId: String, attraction: Attraction) =
        savedApi.addToSaved(userId, attraction)

    suspend fun removeFromSaved(userId: String, attractionId: String) =
        savedApi.removeFromSaved(userId, attractionId)

    suspend fun getPlacesByKeyword(keyword: String): List<TextSearchPlace> {
        return placesApi.searchPlacesByKeyword("$keyword 台灣").results
    }

    suspend fun getPlaceDetails(placeId: String): Attraction {
        val detail = placesApi.getPlaceDetails(placeId).result

        // 🖼️ 嘗試取第一張照片的 photo_reference
        val photoReference = detail.photos?.firstOrNull()?.photo_reference
        val imageUrl = photoReference?.let {
            "https://maps.googleapis.com/maps/api/place/photo" +
                    "?maxwidth=800" +
                    "&photoreference=$it" +
                    "&key=${BuildConfig.MAPS_API_KEY}"
        }

        return Attraction(
            id = placeId,
            name = detail.name ?: "", // 建議補上名稱
            address = detail.formatted_address,
            rating = detail.rating,
            userRatingsTotal = detail.user_ratings_total,
            openingHours = detail.opening_hours?.weekday_text,
            comments = detail.reviews?.map {
                Comment(
                    id = UUID.randomUUID().toString(), // ✅ 補上唯一 id
                    user = it.author_name,
                    rating = it.rating,
                    text = it.text
                )
            },
            imageUrl = imageUrl // ✅ 補上圖片網址
        )
    }
}
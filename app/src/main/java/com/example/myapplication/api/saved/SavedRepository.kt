package com.example.myapplication.api.saved

import com.example.myapplication.api.places.PlacesApiService
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.TextSearchPlace
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
}


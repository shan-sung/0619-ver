package com.example.myapplication.api

import com.example.myapplication.model.Attraction
import javax.inject.Inject

class SavedRepository @Inject constructor(
    private val api: SavedApiService
) {
    suspend fun getSavedAttractions(userId: String): List<Attraction> =
        api.getSavedAttractions(userId)

    suspend fun addToSaved(userId: String, attraction: Attraction) =
        api.addToSaved(userId, attraction)

    suspend fun removeFromSaved(userId: String, attractionId: String) =
        api.removeFromSaved(userId, attractionId)
}

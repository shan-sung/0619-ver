package com.example.myapplication.data.repo

import com.example.myapplication.data.api.PlacesApiService
import com.example.myapplication.data.model.NearbyPlacesResponse
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
}
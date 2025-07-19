package com.example.myapplication.api

import com.example.myapplication.model.NearbyPlacesResponse
import com.example.myapplication.model.PlaceResult
import com.example.myapplication.model.TextSearchPlace
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

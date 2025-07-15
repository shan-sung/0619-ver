package com.example.myapplication.api

import com.example.myapplication.model.Attraction
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SavedApiService {

    @GET("/users/{userId}/saved")
    suspend fun getSavedAttractions(@Path("userId") userId: String): List<Attraction>

    @POST("/users/{userId}/saved")
    suspend fun addToSaved(
        @Path("userId") userId: String,
        @Body attraction: Attraction
    )

    @DELETE("/users/{userId}/saved/{attractionId}")
    suspend fun removeFromSaved(
        @Path("userId") userId: String,
        @Path("attractionId") attractionId: String
    )
}
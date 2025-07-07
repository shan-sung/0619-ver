package com.example.myapplication.api

import com.example.myapplication.data.Travel
import com.example.myapplication.data.TripCreationInfo
import com.example.myapplication.data.TripRequestResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TripsApiService {
    @GET("/trips")
    suspend fun getAllTrips(): List<Travel>

    @POST("/trip-requests")
    suspend fun createTrip(@Body trip: TripCreationInfo): TripRequestResponse
}
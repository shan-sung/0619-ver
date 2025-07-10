package com.example.myapplication.api

import com.example.myapplication.data.ScheduleItem
import com.example.myapplication.data.Travel
import com.example.myapplication.data.TripCreationInfo
import com.example.myapplication.data.TripRequestResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TripsApiService {
    @GET("/trips")
    suspend fun getAllTrips(): List<Travel>

    @POST("/trips")
    suspend fun createTrip(@Body trip: ScheduleItem): Response<ScheduleItem>

    @POST("/trip-requests")
    suspend fun createTrip(@Body trip: TripCreationInfo): TripRequestResponse

    @POST("/trips/{travelId}/schedule")
    suspend fun addScheduleToTrip(
        @Path("travelId") travelId: String,
        @Body item: Map<String, @JvmSuppressWildcards Any>
    ): Response<Unit>
}
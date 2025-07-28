package com.example.myapplication.api.trips

import android.util.Log
import com.example.myapplication.model.Travel
import jakarta.inject.Inject

class TripRepository @Inject constructor(
    private val api: TripsApiService
) {
    suspend fun createTrip(travel: Travel, token: String): Result<Travel> {
        return try {
            Log.d("TripRepository", "Token 傳入：Bearer $token")
            val response = api.createTrip(travel)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to create trip: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
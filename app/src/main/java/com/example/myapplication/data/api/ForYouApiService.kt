package com.example.myapplication.data.api

import com.example.myapplication.data.model.Attraction
import retrofit2.http.GET
import retrofit2.http.Path

interface ForYouApiService {
    @GET("/recommendations/{userId}")
    suspend fun getRecommendations(@Path("userId") userId: String): List<Attraction>
}
package com.example.myapplication.data.api

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.model.NearbyPlacesResponse
import com.example.myapplication.data.model.PlaceDetailsResponse
import com.example.myapplication.data.model.TextSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApiService {
    @GET("maps/api/place/nearbysearch/json")
    suspend fun getNearbyPlaces(
        @Query("location") location: String,
        @Query("radius") radius: Int = 1500,
        @Query("type") type: String,
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ): NearbyPlacesResponse

    @GET("maps/api/place/textsearch/json")
    suspend fun searchPlacesByKeyword(
        @Query("query") query: String = "台灣",  // 例如 "博物館 台灣"
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ): TextSearchResponse

    @GET("maps/api/place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,formatted_address,opening_hours,rating,user_ratings_total,reviews,photos",
        @Query("key") apiKey: String = BuildConfig.MAPS_API_KEY
    ): PlaceDetailsResponse

}
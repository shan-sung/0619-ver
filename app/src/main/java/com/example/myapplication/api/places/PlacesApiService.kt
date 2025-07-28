// 建立 Places API 的 Retrofit 結構
package com.example.myapplication.api.places

import com.example.myapplication.BuildConfig
import com.example.myapplication.model.NearbyPlacesResponse
import com.example.myapplication.model.TextSearchResponse
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

}
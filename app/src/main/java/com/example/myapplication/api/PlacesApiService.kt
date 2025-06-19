// 建立 Places API 的 Retrofit 結構
package com.example.myapplication.api

import com.example.myapplication.data.GeocodingResponse
import com.example.myapplication.data.PlacesSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query
// （定義 API）

interface PlacesApiService {
    @GET("place/nearbysearch/json")
    suspend fun getNearbyAttractions(
        @Query("location") location: String, // 例如 "25.033964,121.564468"
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String = "point_of_interest",
        @Query("key") apiKey: String
    ): PlacesSearchResponse

    @GET("geocode/json")
    suspend fun getGeocodingInfo(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): GeocodingResponse

    @GET("place/textsearch/json")
    suspend fun getTextSearchResults(
        @Query("query") query: String,
        @Query("key") apiKey: String
    ): PlacesSearchResponse

}

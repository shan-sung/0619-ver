// 建立 Places API 的 Retrofit 結構
package com.example.myapplication.api

import com.example.myapplication.model.GeocodingResponse
import com.example.myapplication.model.PlacesSearchResponse
import retrofit2.http.GET // 用 GET 方法去呼叫 API
import retrofit2.http.Query // 用來指定 URL 上的參數

// 建立一個 Retrofit 的「API 介面」
interface PlacesApiService {
    // 函式一：搜尋附近景點
    @GET("place/nearbysearch/json")
    suspend fun getNearbyAttractions(
        @Query("location") location: String, // 例如 "25.033964,121.564468"
        @Query("radius") radius: Int = 5000,
        @Query("type") type: String = "tourist_attraction",
        @Query("key") apiKey: String
    ): PlacesSearchResponse

    // 函式二：取得某經緯度對應的地址（地理編碼）
    @GET("geocode/json")
    suspend fun getGeocodingInfo(
        @Query("latlng") latlng: String,
        @Query("key") apiKey: String
    ): GeocodingResponse

    // 函式三：根據關鍵字搜尋景點（文字搜尋）
    @GET("place/textsearch/json")
    suspend fun getTextSearchResults(
        @Query("query") query: String,
        @Query("type") type: String = "tourist_attraction",
        @Query("region") region: String = "TW",
        @Query("key") apiKey: String
    ): PlacesSearchResponse
}
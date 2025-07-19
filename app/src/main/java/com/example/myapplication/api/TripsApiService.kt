package com.example.myapplication.api

import com.example.myapplication.model.ScheduleItem
import com.example.myapplication.model.Travel
import com.example.myapplication.model.TripCreationInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TripsApiService {
    // 取得所有行程的清單
    @GET("/trips")
    suspend fun getAllTrips(): List<Travel>

    // 提交行程表單 後端回傳完整行程
    @POST("/trip-requests")
    suspend fun createTripFromRequest(@Body trip: TripCreationInfo): Travel

    // 使用者新增行程內的景點
    @POST("/trips/{travelId}/schedule")
    suspend fun addScheduleItem(
        @Path("travelId") travelId: String,
        @Body item: ScheduleItem
    ): Response<ScheduleItem>

    @PATCH("trips/{travelId}/schedules")
    suspend fun updateScheduleItem(
        @Path("travelId") travelId: String,
        @Body updatedItem: ScheduleItem
    ): Response<Unit>

}

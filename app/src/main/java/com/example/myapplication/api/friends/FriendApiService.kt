package com.example.myapplication.api.friends

import com.example.myapplication.model.FriendRequest
import com.example.myapplication.model.FriendRequestBody
import com.example.myapplication.model.FriendResponseBody
import com.example.myapplication.model.UserSummary
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FriendApiService {
    @GET("friends/list") // ✅ 改對應的後端 API
    suspend fun getFriends(): List<UserSummary>

    @GET("friends/pending")
    suspend fun getPendingRequests(): List<FriendRequest>

    @POST("friends/request")
    suspend fun sendFriendRequest(@Body body: FriendRequestBody)

    @GET("friends/sent")
    suspend fun getSentRequests(): List<String>

    @DELETE("friends/request/{toUserId}")
    suspend fun deleteFriendRequest(@Path("toUserId") toUserId: String)

    @POST("friends/respond") // ✅ 用 @Body 傳入資料
    suspend fun respondToRequest(@Body body: FriendResponseBody)

    @GET("users/search")
    suspend fun searchUser(@Query("q") query: String): UserSummary?
}

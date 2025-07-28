package com.example.myapplication.api.friends

import android.util.Log
import com.example.myapplication.model.FriendRequest
import com.example.myapplication.model.FriendRequestBody
import com.example.myapplication.model.FriendResponseBody
import com.example.myapplication.model.UserSummary
import jakarta.inject.Inject

class FriendRepository @Inject constructor(
    private val api: FriendApiService
) {
    suspend fun getFriends(): List<UserSummary> {
        return api.getFriends()
    }

    suspend fun getPendingRequests(): List<FriendRequest> {
        val response = api.getPendingRequests()
        Log.d("FriendRepository", "取得 pending requests：${response.size} 筆")
        return response
    }

    suspend fun sendFriendRequest(toUserId: String) {
        api.sendFriendRequest(FriendRequestBody(to_user_id = toUserId))
    }

    suspend fun getSentRequests(): List<String> {
        return api.getSentRequests()
    }

    suspend fun cancelFriendRequest(toUserId: String) {
        api.deleteFriendRequest(toUserId)
    }

    suspend fun respondToRequest(fromUserId: String, accept: Boolean) {
        api.respondToRequest(FriendResponseBody(from_user_id = fromUserId, accept = accept))
    }

    suspend fun searchUser(query: String): UserSummary? {
        return api.searchUser(query)
    }
}

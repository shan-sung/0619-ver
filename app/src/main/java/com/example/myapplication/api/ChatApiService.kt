package com.example.myapplication.api

import com.example.myapplication.model.ChatMessage
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Inject

interface ChatApiService {
    @GET("chatrooms/{tripId}/messages")
    suspend fun getMessages(@Path("tripId") tripId: String): List<ChatMessage>

    @POST("chatrooms/{tripId}/messages")
    suspend fun sendMessage(@Path("tripId") tripId: String, @Body message: ChatMessage)
}

class ChatRepository @Inject constructor(
    private val chatApi: ChatApiService
) {
    suspend fun loadMessages(tripId: String): List<ChatMessage> {
        return chatApi.getMessages(tripId)
    }

    suspend fun sendMessage(tripId: String, message: ChatMessage) {
        chatApi.sendMessage(tripId, message)
    }
}

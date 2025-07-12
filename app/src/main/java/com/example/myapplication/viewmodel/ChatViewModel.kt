package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private var chatRoomId: String? = null
    private lateinit var webSocket: WebSocket

    fun initChatRoom(chatRoomId: String) {
        this.chatRoomId = chatRoomId
        connectWebSocket(chatRoomId)
    }

    private fun connectWebSocket(chatRoomId: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/ws/chat/$chatRoomId")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val message = Json.decodeFromString<ChatMessage>(text)
                _messages.add(message)
            }
        })
    }

    fun sendMessage(content: String) {
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatRoomId = chatRoomId ?: "",
            senderId = "tempUser123",
            sender = "You",
            message = content,
            timestamp = System.currentTimeMillis()
        )
        _messages.add(msg)

        val jsonMsg = Json.encodeToString(msg)
        webSocket.send(jsonMsg)

        // 模擬 AI 助理
        if (content.contains("travel") || content.contains("recommend")) {
            viewModelScope.launch {
                delay(1000L)
                val aiReply = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    chatRoomId = chatRoomId ?: "",
                    senderId = "ai-bot",
                    sender = "AI",
                    message = "您好，我是智慧旅遊小幫手！請問想去哪裡玩？",
                    timestamp = System.currentTimeMillis()
                )
                _messages.add(aiReply)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        webSocket.close(1000, null)
    }
}

package com.example.myapplication.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    private lateinit var webSocket: WebSocket

    init {
        connectWebSocket()
    }

    private fun connectWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder().url("ws://YOUR_SERVER_URL/ws/chat").build()

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
            sender = "You",
            message = content,
            timestamp = System.currentTimeMillis()
        )
        _messages.add(msg)

        // 模擬 AI 助理回應
        if (content.contains("travel") || content.contains("recommend")) {
            viewModelScope.launch {
                delay(1000L) // 模擬 AI 延遲回覆
                val aiReply = ChatMessage(
                    id = UUID.randomUUID().toString(),
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

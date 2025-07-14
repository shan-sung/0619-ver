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
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    private var chatRoomId: String? = null
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

    fun initChatRoom(chatRoomId: String) {
        if (this.chatRoomId == chatRoomId) return // 已連接就跳過
        this.chatRoomId = chatRoomId
        _messages.clear() // 清除舊聊天室訊息
        loadHistory(chatRoomId)  // 可選：載入歷史訊息
        connectWebSocket(chatRoomId)
    }

    private fun connectWebSocket(chatRoomId: String) {
        val request = Request.Builder()
            .url("ws://10.0.2.2:8080/ws/chat/$chatRoomId")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("WebSocket connected to chatRoomId=$chatRoomId")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val message = Json.decodeFromString<ChatMessage>(text)
                    _messages.add(message)
                } catch (e: Exception) {
                    println("WebSocket message parse error: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("WebSocket error: ${t.message}")
                reconnect() // 可選：自動重連
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket closed: $reason")
            }
        })
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val currentRoomId = chatRoomId ?: return
        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatRoomId = currentRoomId,
            senderId = "tempUser123",
            sender = "You",
            message = content,
            timestamp = System.currentTimeMillis()
        )
        _messages.add(msg)

        val jsonMsg = Json.encodeToString(msg)
        webSocket?.send(jsonMsg)

        // 模擬 AI 助理
        simulateAIResponseIfNeeded(content, currentRoomId)
    }

    private fun simulateAIResponseIfNeeded(content: String, currentRoomId: String) {
        if (content.contains("travel", ignoreCase = true) || content.contains("recommend", ignoreCase = true)) {
            viewModelScope.launch {
                delay(1000L)
                val aiReply = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    chatRoomId = currentRoomId,
                    senderId = "ai-bot",
                    sender = "AI",
                    message = "您好，我是智慧旅遊小幫手！請問想去哪裡玩？",
                    timestamp = System.currentTimeMillis()
                )
                _messages.add(aiReply)
            }
        }
    }

    private fun reconnect() {
        chatRoomId?.let {
            println("Reconnecting to WebSocket...")
            connectWebSocket(it)
        }
    }

    private fun loadHistory(chatRoomId: String) {
        // TODO：從後端 API 抓歷史訊息，可用 Retrofit 或 ktor client
        // viewModelScope.launch {
        //     val history = chatRepository.getMessages(chatRoomId)
        //     _messages.addAll(history)
        // }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            webSocket?.close(1000, "ViewModel cleared")
            println("WebSocket closed normally.")
        } catch (e: Exception) {
            println("Error closing WebSocket: ${e.message}")
        }
    }
}

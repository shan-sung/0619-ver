package com.example.myapplication.viewmodel.myplans.trip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.ChatRepository
import com.example.myapplication.model.ChatMessage
import com.example.myapplication.model.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private var tripId: String? = null

    fun connectToChatroom(tripId: String) {
        this.tripId = tripId
        viewModelScope.launch {
            try {
                val msgs = repository.loadMessages(tripId)
                _messages.value = msgs
            } catch (e: Exception) {
                // TODO: 處理錯誤，顯示 Toast 或記錄 Log
            }
        }
    }

    fun sendMessage(content: String) {
        val user = CurrentUser.user ?: return
        val roomId = tripId ?: return
        val newMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            chatRoomId = roomId,
            senderId = user.id,
            sender = user.username,
            message = content,
            timestamp = System.currentTimeMillis()
        )

        // 本地先加入訊息（立即顯示）
        _messages.value = _messages.value + newMessage

        // 傳送到後端儲存
        viewModelScope.launch {
            try {
                repository.sendMessage(roomId, newMessage)
            } catch (e: Exception) {
                // TODO: 處理錯誤，可考慮從本地移除該筆訊息
            }
        }
    }
}

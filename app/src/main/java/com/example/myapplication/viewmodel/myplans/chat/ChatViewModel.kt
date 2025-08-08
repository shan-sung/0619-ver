package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.ChatRepository
import com.example.myapplication.data.model.ChatMessage
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<ChatMessage>>())
    val uiState: StateFlow<UiState<List<ChatMessage>>> = _uiState

    private var tripId: String? = null

    fun connectToChatroom(tripId: String) {
        this.tripId = tripId
        _uiState.value = UiState(isLoading = true)

        viewModelScope.launch {
            runCatching {
                repository.loadMessages(tripId)
            }.onSuccess {
                _uiState.value = UiState(data = it)
            }.onFailure {
                _uiState.value = UiState(error = it.message)
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
            timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        )

        _uiState.value = _uiState.value.copy(
            data = _uiState.value.data.orEmpty() + newMessage
        )

        viewModelScope.launch {
            runCatching {
                repository.sendMessage(roomId, newMessage)
            }.onFailure {
                Log.e("ChatViewModel", "送出訊息失敗", it)
            }
        }
    }
}

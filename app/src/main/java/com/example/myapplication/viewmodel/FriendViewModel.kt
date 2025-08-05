package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repo.FriendRepository
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.model.FriendRequest
import com.example.myapplication.data.model.UiState
import com.example.myapplication.data.model.UserSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val currentUserId: String? = CurrentUser.user?.id

    private val _friendListState = MutableStateFlow(UiState<List<UserSummary>>())
    val friendListState: StateFlow<UiState<List<UserSummary>>> = _friendListState

    private val _pendingRequestsState = MutableStateFlow(UiState<List<FriendRequest>>())
    val pendingRequestsState: StateFlow<UiState<List<FriendRequest>>> = _pendingRequestsState

    private val _sentRequests = MutableStateFlow<Set<String>>(emptySet())
    val sentRequests: StateFlow<Set<String>> = _sentRequests

    private val _searchResult = MutableStateFlow<UiState<UserSummary?>>(UiState())
    val searchResult: StateFlow<UiState<UserSummary?>> = _searchResult

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadFriendData()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadFriendData() {
        viewModelScope.launch {
            _friendListState.value = UiState(isLoading = true)
            _pendingRequestsState.value = UiState(isLoading = true)

            runCatching {
                val friends = friendRepository.getFriends()
                val sent = friendRepository.getSentRequests()
                val pending = friendRepository.getPendingRequests()

                Triple(friends, sent, pending)
            }.onSuccess { (friends, sent, pending) ->
                _friendListState.value = UiState(data = friends)
                _sentRequests.value = sent.toSet()
                _pendingRequestsState.value = UiState(data = pending)
            }.onFailure {
                _friendListState.value = UiState(error = it.message)
                _pendingRequestsState.value = UiState(error = it.message)
                logError("loadFriendData", it)
            }
        }
    }

    fun toggleFriendRequest(userId: String) {
        viewModelScope.launch {
            val fromUserId = currentUserId ?: return@launch

            runCatching {
                if (_sentRequests.value.contains(userId)) {
                    friendRepository.cancelFriendRequest(userId)
                    _sentRequests.value = _sentRequests.value - userId
                } else {
                    friendRepository.sendFriendRequest(fromUserId, userId)
                    _sentRequests.value = _sentRequests.value + userId
                }

                val updatedPending = friendRepository.getPendingRequests()
                _pendingRequestsState.value = UiState(data = updatedPending)

            }.onFailure {
                logError("toggleFriendRequest", it)
            }
        }
    }

    fun respondToRequest(fromUserId: String, accept: Boolean) {
        viewModelScope.launch {
            runCatching {
                friendRepository.respondToRequest(fromUserId, accept)
                loadFriendData()
            }.onFailure {
                logError("respondToRequest", it)
            }
        }
    }

    fun searchUser(query: String) {
        _searchResult.value = UiState(isLoading = true)
        viewModelScope.launch {
            runCatching {
                friendRepository.searchUser(query)
            }.onSuccess {
                _searchResult.value = UiState(data = it)
            }.onFailure {
                _searchResult.value = UiState(error = it.message)
                logError("searchUser", it)
            }
        }
    }

    fun clearSearch() {
        _searchResult.value = UiState(data = null)
        _searchQuery.value = ""
    }

    private fun logError(tag: String, throwable: Throwable) {
        Log.e("FriendViewModel", "[$tag] ${throwable.message}", throwable)
    }
}

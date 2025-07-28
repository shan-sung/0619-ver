package com.example.myapplication.viewmodel.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.friends.FriendRepository
import com.example.myapplication.model.FriendRequest
import com.example.myapplication.model.UserSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _friendList = MutableStateFlow<List<UserSummary>>(emptyList())
    val friendList = _friendList.asStateFlow()

    private val _searchResult = MutableStateFlow<UserSummary?>(null)
    val searchResult = _searchResult.asStateFlow()

    private val _pendingRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val pendingRequests = _pendingRequests.asStateFlow()

    private val _sentRequests = MutableStateFlow<Set<String>>(emptySet())
    val sentRequests = _sentRequests.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadFriendData()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun loadFriendData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val friends = friendRepository.getFriends()
                Log.d("FriendViewModel", "載入好友共 ${friends.size} 筆")
                _friendList.value = friends

                _sentRequests.value = friendRepository.getSentRequests().toSet()
                _pendingRequests.value = friendRepository.getPendingRequests()
            } catch (e: Exception) {
                Log.e("FriendViewModel", "載入好友資料失敗", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun toggleFriendRequest(userId: String) {
        viewModelScope.launch {
            try {
                if (_sentRequests.value.contains(userId)) {
                    friendRepository.cancelFriendRequest(userId)
                    _sentRequests.value = _sentRequests.value - userId
                } else {
                    friendRepository.sendFriendRequest(userId)
                    _sentRequests.value = _sentRequests.value + userId
                }
                _pendingRequests.value = friendRepository.getPendingRequests()
            } catch (e: Exception) {
                Log.e("FriendViewModel", "切換好友邀請失敗", e)
            }
        }
    }

    fun respondToRequest(fromUserId: String, accept: Boolean) {
        viewModelScope.launch {
            try {
                friendRepository.respondToRequest(fromUserId, accept)
                loadFriendData()
            } catch (e: Exception) {
                Log.e("FriendViewModel", "處理好友邀請失敗", e)
            }
        }
    }

    fun searchUser(query: String) {
        viewModelScope.launch {
            try {
                _searchResult.value = friendRepository.searchUser(query)
            } catch (e: Exception) {
                _searchResult.value = null
                Log.e("FriendViewModel", "搜尋使用者失敗", e)
            }
        }
    }

    fun clearSearch() {
        _searchResult.value = null
        _searchQuery.value = ""
    }
}
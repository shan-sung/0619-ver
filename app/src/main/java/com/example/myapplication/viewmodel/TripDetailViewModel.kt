package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.TripsApiService
import com.example.myapplication.data.model.AddMembersRequest
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.Travel
import com.example.myapplication.data.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val tripsApi: TripsApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<Travel>())
    val uiState: StateFlow<UiState<Travel>> = _uiState

    /**
     * 載入單一行程
     */
    fun fetchTravelById(travelId: String) {
        _uiState.value = UiState(isLoading = true)

        viewModelScope.launch {
            runCatching {
                tripsApi.getAllTrips().find { it._id == travelId }
            }.onSuccess { travel ->
                if (travel != null) {
                    _uiState.value = UiState(data = travel)
                } else {
                    _uiState.value = UiState(error = "找不到對應行程")
                }
            }.onFailure {
                _uiState.value = UiState(error = it.message ?: "載入失敗")
                logError("fetchTravelById", it)
            }
        }
    }

    /**
     * 編輯行程項目
     */
    fun updateScheduleItemAndRefresh(
        travelId: String,
        day: Int,
        index: Int,
        updatedItem: ScheduleItem,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            runCatching {
                tripsApi.updateScheduleItem(travelId, day, index, updatedItem)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    refreshTravel(travelId, onResult)
                } else {
                    logError("updateScheduleItem", Exception("code=${response.code()}"))
                    onResult(false)
                }
            }.onFailure {
                logError("updateScheduleItem", it)
                onResult(false)
            }
        }
    }

    /**
     * 新增行程項目並 refresh
     */
    fun submitScheduleItemSafely(
        travelId: String,
        item: ScheduleItem,
        onResult: (Boolean) -> Unit
    ) {
        if (_uiState.value.isLoading) return
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            runCatching {
                tripsApi.addScheduleItem(travelId, item)
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    refreshTravel(travelId, onResult)
                } else {
                    onResult(false)
                }
            }.onFailure {
                logError("submitScheduleItem", it)
                onResult(false)
            }.also {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * 邀請好友加入行程
     */
    fun inviteFriends(tripId: String, friendIds: List<String>, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            runCatching {
                tripsApi.addMembersToTrip(tripId, AddMembersRequest(friendIds))
            }.onSuccess {
                if (it.isSuccessful) {
                    fetchTravelById(tripId)  // refresh
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }.onFailure {
                logError("inviteFriends", it)
                onComplete(false)
            }
        }
    }

    /**
     * 刷新旅程資料並回傳 onResult 成功與否
     */
    private suspend fun refreshTravel(travelId: String, onResult: (Boolean) -> Unit) {
        runCatching {
            tripsApi.getAllTrips().find { it._id == travelId }
        }.onSuccess { updatedTrip ->
            if (updatedTrip != null) {
                _uiState.value = UiState(data = updatedTrip)
                onResult(true)
            } else {
                logError("refreshTravel", Exception("找不到 tripId=$travelId"))
                onResult(false)
            }
        }.onFailure {
            logError("refreshTravel", it)
            onResult(false)
        }
    }

    private fun logError(tag: String, throwable: Throwable) {
        Log.e("TripDetailViewModel", "[$tag] ${throwable.message}", throwable)
    }
}
package com.example.myapplication.viewmodel.myplans

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.TripsApiService
import com.example.myapplication.data.model.AddMembersRequest
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.Travel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val tripsApi: TripsApiService
) : ViewModel() {

    private val _travel = MutableStateFlow<Travel?>(null)
    val travel: StateFlow<Travel?> = _travel

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * 載入行程
     */
    fun fetchTravelById(travelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val allTrips = tripsApi.getAllTrips()
                _travel.value = allTrips.find { it._id == travelId }
                if (_travel.value == null) {
                    _error.value = "找不到對應行程"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 編輯行程項目（指定第幾天的第幾個 index）
     */
    fun updateScheduleItemAndRefresh(
        travelId: String,
        day: Int,
        index: Int,
        updatedItem: ScheduleItem,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = tripsApi.updateScheduleItem(travelId, day, index, updatedItem)
                if (response.isSuccessful) {
                    val updatedTrip = tripsApi.getAllTrips().find { it._id == travelId }
                    if (updatedTrip != null) {
                        _travel.value = updatedTrip
                        onResult(true)
                        return@launch
                    } else {
                        Log.e("TripVM", "更新成功但無法取得 tripId=$travelId 的行程")
                    }
                } else {
                    Log.e("TripVM", "更新失敗: code=${response.code()}, errorBody=${response.errorBody()?.string()}")
                }
                onResult(false)
            } catch (e: Exception) {
                Log.e("TripVM", "更新行程失敗", e)
                onResult(false)
            }
        }
    }

    /**
     * 新增 schedule 並自動 refresh，防止重複送出
     */
    fun submitScheduleItemSafely(
        travelId: String,
        item: ScheduleItem,
        onResult: (Boolean) -> Unit
    ) {
        if (_isLoading.value) return
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = tripsApi.addScheduleItem(travelId, item)
                if (response.isSuccessful) {
                    val updatedTrip = tripsApi.getAllTrips().find { it._id == travelId }
                    if (updatedTrip != null) {
                        _travel.value = updatedTrip
                        onResult(true)
                    } else {
                        onResult(false)
                    }
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun inviteFriends(tripId: String, friendIds: List<String>, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = tripsApi.addMembersToTrip(tripId, AddMembersRequest(friendIds))
                if (result.isSuccessful) {
                    fetchTravelById(tripId) // ✅ 更新成員清單
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
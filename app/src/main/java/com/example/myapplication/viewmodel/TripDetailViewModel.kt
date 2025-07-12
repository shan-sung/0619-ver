package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.TripsApiService
import com.example.myapplication.model.ScheduleItem
import com.example.myapplication.model.Travel
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
     * 根據 ID 抓取該筆 Travel 並更新狀態
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
     * 非同步方式提交行程項目，並更新行程資料
     */
    fun submitScheduleItemAndRefresh(
        travelId: String,
        item: ScheduleItem,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = tripsApi.addScheduleItem(travelId, item)
                if (response.isSuccessful) {
                    // 重新取得該筆 Travel 更新畫面
                    val updatedTrip = tripsApi.getAllTrips().find { it._id == travelId }
                    if (updatedTrip != null) {
                        _travel.value = updatedTrip
                        onResult(true)
                        return@launch
                    }
                }
                onResult(false)
            } catch (e: Exception) {
                Log.e("TripVM", "提交 ScheduleItem 發生錯誤", e)
                onResult(false)
            }
        }
    }

    /**
     * 若只需要發送不更新狀態，可用此
     */
    fun submitScheduleItem(
        travelId: String,
        item: ScheduleItem,
        callback: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = tripsApi.addScheduleItem(travelId, item)
                callback(response.isSuccessful)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }
    }
}


package com.example.myapplication.viewmodel.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.TripsApiService
import com.example.myapplication.model.CurrentUser
import com.example.myapplication.model.Travel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val apiService: TripsApiService
) : ViewModel() {

    // -----------------------------
    // 1. 所有旅程資料來源（核心原始資料）
    // -----------------------------
    private val _trips = MutableStateFlow<List<Travel>>(emptyList())
    val trips: StateFlow<List<Travel>> = _trips

    // -----------------------------
    // 2. 衍生出的旅程分類（依據目前使用者）
    // -----------------------------
    val myCreatedTrips: StateFlow<List<Travel>> = _trips.map { list ->
        list.filter { it.userId == CurrentUser.user?.id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val myJoinedTrips: StateFlow<List<Travel>> = _trips.map { list ->
        list.filter { CurrentUser.user?.id in it.members }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val upcomingTrips: StateFlow<List<Travel>> = _trips.map { list ->
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        list.filter {
            try {
                LocalDate.parse(it.startDate, formatter).isAfter(today) ||  // 出發日在今天之後
                        LocalDate.parse(it.startDate, formatter).isEqual(today)     // 或是今天出發
            } catch (e: Exception) {
                false
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val pastTrips: StateFlow<List<Travel>> = _trips.map { list ->
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        list.filter {
            try {
                LocalDate.parse(it.endDate, formatter).isBefore(today)      // 結束日在今天之前
            } catch (e: Exception) {
                false
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())


    // -----------------------------
    // 3. UI 狀態控制（載入中 / 錯誤）
    // -----------------------------
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // -----------------------------
    // 4. 資料載入功能
    // -----------------------------
    fun fetchAllTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _trips.value = apiService.getAllTrips()
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

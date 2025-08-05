package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.api.TripsApiService
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.model.Travel
import com.example.myapplication.data.model.UiState
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

    // ✅ 統一 UI 狀態（包含 trips + loading + error）
    private val _uiState = MutableStateFlow(UiState<List<Travel>>())
    val uiState: StateFlow<UiState<List<Travel>>> = _uiState

    // ✅ 所有 trip 來源統一從 uiState.data
    private val trips: StateFlow<List<Travel>> = _uiState.map { it.data.orEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ✅ 衍生分類
    val myCreatedTrips: StateFlow<List<Travel>> = trips.map { list ->
        list.filter { it.userId == CurrentUser.user?.id }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val myJoinedTrips: StateFlow<List<Travel>> = trips.map { list ->
        list.filter { CurrentUser.user?.id in it.members }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val upcomingTrips: StateFlow<List<Travel>> = trips.map { list ->
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        list.filter {
            try {
                LocalDate.parse(it.startDate, formatter).let { date ->
                    date.isAfter(today) || date.isEqual(today)
                }
            } catch (_: Exception) {
                false
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val pastTrips: StateFlow<List<Travel>> = trips.map { list ->
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        list.filter {
            try {
                LocalDate.parse(it.endDate, formatter).isBefore(today)
            } catch (_: Exception) {
                false
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // ✅ 載入旅程
    fun fetchAllTrips() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            runCatching {
                apiService.getAllTrips()
            }.onSuccess {
                _uiState.value = UiState(data = it)
            }.onFailure {
                _uiState.value = UiState(error = it.message ?: "載入失敗")
                logError("fetchAllTrips", it)
            }
        }
    }

    private fun logError(tag: String, throwable: Throwable) {
        Log.e("TripsViewModel", "[$tag] ${throwable.message}", throwable)
    }
}

package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.TripsApiService
import com.example.myapplication.data.Travel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val apiService: TripsApiService
) : ViewModel() {

    private val _trips = MutableStateFlow<List<Travel>>(emptyList())
    val trips: StateFlow<List<Travel>> = _trips

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val createdTrips: StateFlow<List<Travel>> = _trips.map { list ->
        list.filter { it.created == true }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val joinedTrips: StateFlow<List<Travel>> = _trips.map { list ->
        list.filter { it.created == false }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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

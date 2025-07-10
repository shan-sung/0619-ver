package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.TripsApiService
import com.example.myapplication.data.ScheduleItem
import com.example.myapplication.data.Travel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val tripsApi: TripsApiService
) : ViewModel() {

    fun getTravelById(travelId: String): StateFlow<Travel?> {
        val state = MutableStateFlow<Travel?>(null)
        viewModelScope.launch {
            val allTrips = tripsApi.getAllTrips()
            state.value = allTrips.find { it._id == travelId }
        }
        return state
    }

    fun submitScheduleItem(travelId: String, body: Map<String, Any>, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = tripsApi.addScheduleToTrip(travelId, body)
                callback(response.isSuccessful)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }
    }
}

package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.TripsApiService
import com.example.myapplication.model.CurrentUser
import com.example.myapplication.model.DummyUser
import com.example.myapplication.model.TripCreationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TripCreationViewModel @Inject constructor(
    private val tripsApiService: TripsApiService
) : ViewModel() {

    private val _tripInfo = MutableStateFlow(TripCreationInfo())
    val tripInfo: StateFlow<TripCreationInfo> = _tripInfo.asStateFlow()

    private val _step = MutableStateFlow(0)
    val step: StateFlow<Int> = _step.asStateFlow()

    fun submitTrip() {
        viewModelScope.launch {
            try {
                val currentUser = CurrentUser.user
                if (currentUser != null) {
                    val trip = tripInfo.value.copy(userId = currentUser.id)
                    val createdTrip = tripsApiService.createTripFromRequest(trip)
                    Log.d("TripCreation", "建立成功：$createdTrip")
                    resetTrip()
                } else {
                    Log.e("TripCreation", "未登入使用者，無法建立行程")
                }
            } catch (e: Exception) {
                Log.e("TripCreation", "建立失敗", e)
            }
        }
    }


    fun resetTrip() {
        _tripInfo.value = TripCreationInfo()
        _step.value = 0
    }

    fun updateStartDate(date: LocalDate) {
        _tripInfo.update { it.copy(startDate = date) }
    }

    fun updateEndDate(date: LocalDate) {
        _tripInfo.update { it.copy(endDate = date) }
    }

    fun updateCities(selected: List<String>) {
        _tripInfo.update { it.copy(cities = selected) }
    }

    fun updateTitle(title: String) {
        _tripInfo.update { it.copy(title = title) }
    }

    fun updatePeopleCount(count: Int) {
        _tripInfo.update { it.copy(peopleCount = count) }
    }

    fun updateAgeRange(range: String) {
        _tripInfo.update { it.copy(averageAgeRange = range) }
    }

    fun updatePreferences(pref: List<String>) {
        _tripInfo.update { it.copy(preferences = pref) }
    }

    fun updateTransportOptions(options: List<String>) {
        _tripInfo.update { it.copy(transportOptions = options) }
    }

    fun updateBudget(amount: Int) {
        _tripInfo.update { it.copy(budget = amount) }
    }

    fun nextStep() {
        _step.update { it + 1 }
    }

    fun prevStep() {
        _step.update { (it - 1).coerceAtLeast(0) }
    }
}

package com.example.myapplication.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.UiState
import com.example.myapplication.data.model.buildPhotoUrl
import com.example.myapplication.data.repo.PlacesRepository
import com.example.myapplication.util.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val repository: PlacesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<Attraction>>())
    val uiState: StateFlow<UiState<List<Attraction>>> = _uiState

    private val _selectedAttractionDetail = MutableStateFlow<Attraction?>(null)
    val selectedAttractionDetail: StateFlow<Attraction?> = _selectedAttractionDetail

    fun loadNearbyAttractions(context: Context) {
        LocationUtils.getCurrentLocation(context) { location ->
            if (location == null) return@getCurrentLocation

            val locationStr = "${location.latitude},${location.longitude}"
            _uiState.value = UiState(isLoading = true)

            viewModelScope.launch {
                runCatching {
                    repository.getNearbyAttractionsAndRestaurants(locationStr)
                }.onSuccess { response ->
                    val result = response.results.take(6).map {
                        val photoRef = it.photos?.firstOrNull()?.photo_reference
                        Attraction(
                            id = it.place_id,
                            name = it.name,
                            city = "",  // 如需地點解析可擴充
                            country = "",
                            rating = it.rating,
                            tags = it.types ?: emptyList(),
                            description = it.vicinity,
                            imageUrl = photoRef?.let { buildPhotoUrl(it) }
                        )
                    }
                    _uiState.value = UiState(data = result)
                }.onFailure {
                    _uiState.value = UiState(error = it.message ?: "載入失敗")
                    logError("loadNearbyAttractions", it)
                }
            }
        }
    }

    fun loadAttractionDetail(placeId: String) {
        viewModelScope.launch {
            runCatching {
                repository.getPlaceDetails(placeId)
            }.onSuccess {
                _selectedAttractionDetail.value = it
            }.onFailure {
                _selectedAttractionDetail.value = null
                logError("loadAttractionDetail", it)
            }
        }
    }

    fun clearSelectedAttraction() {
        _selectedAttractionDetail.value = null
    }

    private fun logError(tag: String, throwable: Throwable) {
        Log.e("AttractionsViewModel", "[$tag] ${throwable.message}", throwable)
    }
}

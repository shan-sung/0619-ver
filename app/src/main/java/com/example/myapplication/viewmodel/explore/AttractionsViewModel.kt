package com.example.myapplication.viewmodel.explore

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repo.PlacesRepository
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.buildPhotoUrl
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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList())
    val attractions: StateFlow<List<Attraction>> = _attractions
    private val _selectedAttractionDetail = MutableStateFlow<Attraction?>(null)
    val selectedAttractionDetail: StateFlow<Attraction?> = _selectedAttractionDetail

    fun fetchNearbyAttractions(context: Context) {
        LocationUtils.getCurrentLocation(context) { location ->
            if (location != null) {
                viewModelScope.launch {
                    try {
                        val locationStr = "${location.latitude},${location.longitude}"
                        val response = repository.getNearbyAttractionsAndRestaurants(locationStr)
                        _attractions.value = response.results.take(6).map {
                            val ref = it.photos?.firstOrNull()?.photo_reference
                            Attraction(
                                id = it.place_id,
                                name = it.name,
                                city = "",
                                country = "",
                                rating = it.rating,
                                tags = it.types ?: emptyList(),
                                description = it.vicinity,
                                imageUrl = ref?.let { buildPhotoUrl(it) }
                            )
                        }

                    } catch (e: Exception) {
                        Log.e("AttractionsViewModel", "Error: ${e.message}")
                    }
                }
            }
        }
    }
    fun loadAttractionDetail(placeId: String) {
        viewModelScope.launch {
            try {
                val detail = repository.getPlaceDetails(placeId)
                _selectedAttractionDetail.value = detail
            } catch (e: Exception) {
                Log.e("AttractionsViewModel", "載入詳細資料失敗", e)
                _selectedAttractionDetail.value = null
            }
        }
    }
}
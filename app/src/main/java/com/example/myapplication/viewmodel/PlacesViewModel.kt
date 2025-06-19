package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.BuildConfig
import com.example.myapplication.api.PlacesApiService
import com.example.myapplication.data.Attraction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val apiService: PlacesApiService
) : ViewModel() {

    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList())
    val attractions: StateFlow<List<Attraction>> = _attractions
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchNearbyAttractions(location: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getNearbyAttractions(
                    location = location,
                    radius = 5000,
                    apiKey = BuildConfig.MAPS_API_KEY
                )

                Log.d("API DEBUG", "status=${response.status}, size=${response.results.size}, location=$location")

                val mapped = response.results.map { place ->
                    val lat = place.geometry.location.lat
                    val lng = place.geometry.location.lng
                    val latlng = "$lat,$lng"

                    // 額外呼叫 Geocoding API
                    val geo = apiService.getGeocodingInfo(latlng, BuildConfig.MAPS_API_KEY)
                    val geoResult = geo.results.firstOrNull()

                    val city = geoResult?.address_components?.firstOrNull {
                        it.types.contains("administrative_area_level_1")
                    }?.long_name ?: "未知縣市"

                    val district = geoResult?.address_components?.firstOrNull {
                        it.types.contains("administrative_area_level_2")
                    }?.long_name ?: "未知區"

                    Attraction(
                        id = place.place_id,
                        name = place.name,
                        city = "$city$district", // ✅ 這裡顯示「台北市信義區」
                        country = "Taiwan",
                        rating = place.rating ?: 0.0,
                        imageUrl = place.photos?.firstOrNull()?.photo_reference?.let { ref ->
                            buildPhotoUrl(ref)
                        }
                    )
                }

                _attractions.value = mapped

            } catch (e: Exception) {
                Log.e("API", "錯誤：${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchAttractionsByText(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getTextSearchResults(query, BuildConfig.MAPS_API_KEY)
                val mapped = response.results.map { place ->
                    Attraction(
                        id = place.place_id,
                        name = place.name,
                        city = place.vicinity ?: "未知地區",
                        country = "Taiwan",
                        rating = place.rating ?: 0.0,
                        imageUrl = place.photos?.firstOrNull()?.photo_reference?.let {
                            buildPhotoUrl(it)
                        }
                    )
                }
                _attractions.value = mapped

            } catch (e: Exception) {
                Log.e("TextSearch", "搜尋錯誤：${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun buildPhotoUrl(photoRef: String): String {
        return "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=400&photo_reference=$photoRef&key=${BuildConfig.MAPS_API_KEY}"
    }
}


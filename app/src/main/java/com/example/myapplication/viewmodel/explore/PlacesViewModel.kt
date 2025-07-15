package com.example.myapplication.viewmodel.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.BuildConfig
import com.example.myapplication.api.PlacesApiService
import com.example.myapplication.model.Attraction
import com.example.myapplication.util.buildPhotoUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 負責從 Google Maps API 取得「附近的景點資訊」
@HiltViewModel
class AttractionsViewModel @Inject constructor( // @Inject constructor(...): 使用 Hilt 注入 PlacesApiService，負責與 Google Maps API 溝通。
    private val apiService: PlacesApiService // 告訴Hilt此為我需要的東西，請幫我注入
) : ViewModel() {

    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList()) // 私有可變的 StateFlow，用來儲存目前取得的景點清單。
    val attractions: StateFlow<List<Attraction>> = _attractions // 公開只讀的版本給 UI 使用。
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    // 取得附近景點
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
                    async {
                        val lat = place.geometry.location.lat
                        val lng = place.geometry.location.lng
                        val latlng = "$lat,$lng"
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
                            city = "$city $district",
                            country = "Taiwan",
                            rating = place.rating ?: 0.0,
                            category = "Art Museum",
                            imageUrl = place.photos?.firstOrNull()?.photo_reference?.let { ref ->
                                buildPhotoUrl(ref)
                            }
                        )
                    }
                }.awaitAll()
                _attractions.value = mapped
            } catch (e: Exception) {
                Log.e("API", "錯誤：${e.message}", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
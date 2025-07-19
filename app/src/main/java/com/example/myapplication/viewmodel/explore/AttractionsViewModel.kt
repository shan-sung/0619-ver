package com.example.myapplication.viewmodel.explore

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.PlacesRepository
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.buildPhotoUrl
import com.example.myapplication.util.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

val typeMap = mapOf(
    "tourist_attraction" to "旅遊景點",
    "museum" to "博物館",
    "restaurant" to "餐廳",
    "shopping_mall" to "購物中心",
    "cafe" to "咖啡廳",
    "bar" to "酒吧",
    "night_club" to "夜店",
    "park" to "公園",
    "amusement_park" to "遊樂園",
    "aquarium" to "水族館",
    "art_gallery" to "藝術館",
    "zoo" to "動物園",
    "movie_theater" to "電影院",
    "stadium" to "體育館",
    "library" to "圖書館",
    "church" to "教堂",
    "hindu_temple" to "印度廟",
    "mosque" to "清真寺",
    "synagogue" to "猶太會堂",
    "city_hall" to "市政廳",
    "embassy" to "大使館",
    "landmark" to "地標",
    "point_of_interest" to "景點",
    "establishment" to "設施",
    "train_station" to "火車站",
    "subway_station" to "捷運站",
    "bus_station" to "公車站",
    "airport" to "機場",
    "lodging" to "住宿",
    "hotel" to "飯店",
    "bakery" to "烘焙坊",
    "book_store" to "書店",
    "clothing_store" to "服飾店",
    "convenience_store" to "便利商店",
    "department_store" to "百貨公司",
    "electronics_store" to "電子產品店",
    "furniture_store" to "家具店",
    "jewelry_store" to "珠寶店",
    "movie_rental" to "影片出租店",
    "pet_store" to "寵物店",
    "pharmacy" to "藥局",
    "supermarket" to "超市",
    "tour_agency" to "旅行社"
)

@HiltViewModel
class AttractionsViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _attractions = MutableStateFlow<List<Attraction>>(emptyList())
    val attractions: StateFlow<List<Attraction>> = _attractions

    fun fetchNearbyAttractions(context: Context) {
        LocationUtils.getCurrentLocation(context) { location ->
            if (location != null) {
                viewModelScope.launch {
                    try {
                        val locationStr = "${location.latitude},${location.longitude}"
                        val response = placesRepository.getNearbyAttractionsAndRestaurants(locationStr)
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
}
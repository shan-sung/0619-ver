package com.example.myapplication.viewmodel.saved

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repo.SavedRepository
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val savedRepo: SavedRepository
) : ViewModel() {

    private val _savedAttractions = MutableStateFlow<List<Attraction>>(emptyList())
    val savedAttractions: StateFlow<List<Attraction>> = _savedAttractions

    private val _forYouAttractions = MutableStateFlow<List<Attraction>>(emptyList())
    val forYouAttractions: StateFlow<List<Attraction>> = _forYouAttractions

    private val _selectedAttractionDetail = MutableStateFlow<Attraction?>(null)
    val selectedAttractionDetail: StateFlow<Attraction?> = _selectedAttractionDetail

    init {
        // 初始化載入
        fetchSavedAttractions()
        fetchForYouAttractions()
    }

    fun fetchSavedAttractions() {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                _savedAttractions.value = savedRepo.getSavedAttractions(userId)
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error fetching saved attractions", e)
            }
        }
    }

    fun fetchForYouAttractions() {
        viewModelScope.launch {
            try {
                // TODO: 換成個人化推薦來源（例如分析偏好）
                _forYouAttractions.value = mockForYouPlaces()
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error fetching for-you attractions", e)
            }
        }
    }

    fun addToSaved(attraction: Attraction) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                savedRepo.addToSaved(userId, attraction)
                fetchSavedAttractions()
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error adding attraction", e)
            }
        }
    }

    fun removeFromSaved(attraction: Attraction) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                savedRepo.removeFromSaved(userId, attraction.id)
                fetchSavedAttractions()
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error removing attraction", e)
            }
        }
    }

    fun loadAttractionDetail(placeId: String) {
        viewModelScope.launch {
            try {
                _selectedAttractionDetail.value = savedRepo.getPlaceDetails(placeId)
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error loading place detail", e)
            }
        }
    }

    private fun mockForYouPlaces(): List<Attraction> = listOf(
        Attraction(
            id = "f1",
            name = "華山文創園區",
            address = "台北市中正區",
            rating = 4.4,
            userRatingsTotal = 2200,
            tags = listOf("culture", "shopping"),
            city = "台北",
            country = "台灣",
            description = "展覽與文創聚落",
            imageUrl = "https://via.placeholder.com/150"
        )
    )
}
package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.repo.ForYouRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ForYouViewModel @Inject constructor(
    private val forYouRepo: ForYouRepository
) : ViewModel() {

    private val _forYouAttractions = MutableStateFlow<List<Attraction>>(emptyList())
    val forYouAttractions: StateFlow<List<Attraction>> = _forYouAttractions

    private val _selectedAttractionDetail = MutableStateFlow<Attraction?>(null)
    val selectedAttractionDetail: StateFlow<Attraction?> = _selectedAttractionDetail

    init {
        fetchForYouAttractions()
    }

    fun fetchForYouAttractions() {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                _forYouAttractions.value = forYouRepo.getRecommendations(userId)
            } catch (e: Exception) {
                Log.e("ForYouViewModel", "Error fetching recommendations", e)
            }
        }
    }

    fun loadAttractionDetail(placeId: String) {
        viewModelScope.launch {
            try {
                _selectedAttractionDetail.value = forYouRepo.getPlaceDetails(placeId)
            } catch (e: Exception) {
                Log.e("ForYouViewModel", "Error loading detail", e)
            }
        }
    }

    fun addToSaved(attraction: Attraction) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                forYouRepo.addToSaved(userId, attraction)
            } catch (e: Exception) {
                Log.e("ForYouViewModel", "Error adding to saved", e)
            }
        }
    }
}

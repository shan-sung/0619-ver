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
    private val repository: SavedRepository
) : ViewModel() {

    private val _savedAttractions = MutableStateFlow<List<Attraction>>(emptyList())
    val savedAttractions: StateFlow<List<Attraction>> = _savedAttractions

    private val _selectedAttractionDetail = MutableStateFlow<Attraction?>(null)
    val selectedAttractionDetail: StateFlow<Attraction?> = _selectedAttractionDetail

    init {
        fetchSavedAttractions()
    }

    fun fetchSavedAttractions() {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                _savedAttractions.value = repository.getSavedAttractions(userId)
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error fetching saved attractions", e)
            }
        }
    }

    fun removeFromSaved(attraction: Attraction) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                repository.removeFromSaved(userId, attraction.id)
                fetchSavedAttractions()
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error removing attraction", e)
            }
        }
    }

    fun loadAttractionDetail(placeId: String) {
        viewModelScope.launch {
            try {
                _selectedAttractionDetail.value = repository.getPlaceDetails(placeId)
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error loading attraction detail", e)
            }
        }
    }
}

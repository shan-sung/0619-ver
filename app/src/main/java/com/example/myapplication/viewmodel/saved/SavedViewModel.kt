package com.example.myapplication.viewmodel.saved

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repo.SavedRepository
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// 重構後：SavedViewModel
@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: SavedRepository
) : ViewModel() {

    private val _savedState = MutableStateFlow(UiState<List<Attraction>>())
    val savedState: StateFlow<UiState<List<Attraction>>> = _savedState

    private val _selectedAttraction = MutableStateFlow<Attraction?>(null)
    val selectedAttraction: StateFlow<Attraction?> = _selectedAttraction

    init {
        fetchSavedAttractions()
    }

    fun fetchSavedAttractions() {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            _savedState.value = UiState(isLoading = true)
            try {
                val data = repository.getSavedAttractions(userId)
                _savedState.value = UiState(data = data)
            } catch (e: Exception) {
                _savedState.value = UiState(error = e.message)
            }
        }
    }

    fun addToSaved(attraction: Attraction) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                repository.addToSaved(userId, attraction)
                val updated = _savedState.value.data.orEmpty() + attraction
                _savedState.value = UiState(data = updated)
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error adding to saved", e)
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
                Log.e("SavedViewModel", "Error removing", e)
            }
        }
    }

    fun loadAttractionDetail(placeId: String) {
        viewModelScope.launch {
            try {
                _selectedAttraction.value = repository.getPlaceDetails(placeId)
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Detail load error", e)
            }
        }
    }
}
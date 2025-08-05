package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.model.UiState
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

    private val _recommendState = MutableStateFlow(UiState<List<Attraction>>())
    val recommendState: StateFlow<UiState<List<Attraction>>> = _recommendState

    private val _selectedAttraction = MutableStateFlow<Attraction?>(null)
    val selectedAttraction: StateFlow<Attraction?> = _selectedAttraction

    init {
        fetchForYouAttractions()
    }

    fun fetchForYouAttractions() {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            _recommendState.value = UiState(isLoading = true)
            try {
                val data = forYouRepo.getRecommendations(userId)
                _recommendState.value = UiState(data = data)
            } catch (e: Exception) {
                _recommendState.value = UiState(error = e.message)
            }
        }
    }

    fun loadAttractionDetail(placeId: String) {
        viewModelScope.launch {
            try {
                _selectedAttraction.value = forYouRepo.getPlaceDetails(placeId)
            } catch (e: Exception) {
                Log.e("ForYouViewModel", "Error loading detail", e)
            }
        }
    }
}

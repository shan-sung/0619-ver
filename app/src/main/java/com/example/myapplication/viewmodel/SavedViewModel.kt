package com.example.myapplication.viewmodel

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

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val repository: SavedRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<Attraction>>())
    val uiState: StateFlow<UiState<List<Attraction>>> = _uiState

    private val _selectedAttractionDetail = MutableStateFlow<Attraction?>(null)
    val selectedAttractionDetail: StateFlow<Attraction?> = _selectedAttractionDetail

    init {
        loadSavedAttractions()
    }

    fun loadSavedAttractions() {
        val userId = CurrentUser.user?.id ?: return
        updateUiStateLoading()
        viewModelScope.launch {
            runCatching {
                repository.getSavedAttractions(userId)
            }.onSuccess { attractions ->
                _uiState.value = UiState(data = attractions)
            }.onFailure { e ->
                _uiState.value = UiState(error = e.message ?: "無法載入收藏")
                logError("loadSavedAttractions", e)
            }
        }
    }

    fun addToSaved(attraction: Attraction) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            runCatching {
                repository.addToSaved(userId, attraction)
            }.onSuccess {
                val updatedList = _uiState.value.data.orEmpty() + attraction
                _uiState.value = UiState(data = updatedList)
            }.onFailure {
                logError("addToSaved", it)
            }
        }
    }

    fun removeFromSaved(attraction: Attraction) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            runCatching {
                repository.removeFromSaved(userId, attraction.id)
            }.onSuccess {
                loadSavedAttractions()
            }.onFailure {
                logError("removeFromSaved", it)
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
                logError("loadAttractionDetail", it)
            }
        }
    }

    private fun updateUiStateLoading() {
        _uiState.value = UiState(isLoading = true)
    }

    private fun logError(tag: String, throwable: Throwable) {
        Log.e("SavedViewModel", "[$tag] ${throwable.message}", throwable)
    }
}

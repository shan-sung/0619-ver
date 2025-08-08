package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.UiState
import com.example.myapplication.data.repo.PlacesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<Attraction>>())
    val uiState: StateFlow<UiState<List<Attraction>>> = _uiState

    private val _selectedAttraction = MutableStateFlow<Attraction?>(null)
    val selectedAttraction: StateFlow<Attraction?> = _selectedAttraction

    private var searchJob: Job? = null

    fun debouncedSearch(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            _uiState.value = UiState(data = emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            delay(400)
            _uiState.value = UiState(isLoading = true)

            runCatching {
                placesRepository.searchPlaces(query)
            }.onSuccess { results ->
                _uiState.value = UiState(data = results)
            }.onFailure { e ->
                _uiState.value = UiState(error = e.message ?: "搜尋失敗")
                logError("debouncedSearch", e)
            }
        }
    }

    fun setSelectedAttraction(attraction: Attraction) {
        _selectedAttraction.value = attraction
    }

    fun clearSelectedAttraction() {
        _selectedAttraction.value = null
    }

    private fun logError(tag: String, throwable: Throwable) {
        Log.e("SearchViewModel", "[$tag] ${throwable.message}", throwable)
    }
}

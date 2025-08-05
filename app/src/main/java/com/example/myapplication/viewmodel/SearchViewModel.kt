package com.example.myapplication.viewmodel

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

    private val _searchResult = MutableStateFlow(UiState<List<Attraction>>())
    val searchResult: StateFlow<UiState<List<Attraction>>> = _searchResult

    private val _selectedAttraction = MutableStateFlow<Attraction?>(null)
    val selectedAttraction: StateFlow<Attraction?> = _selectedAttraction

    fun setSelectedAttraction(attraction: Attraction) {
        _selectedAttraction.value = attraction
    }

    fun clearSelectedAttraction() {
        _selectedAttraction.value = null
    }

    private var searchJob: Job? = null

    fun debouncedSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _searchResult.value = UiState(data = emptyList())
            return
        }

        searchJob = viewModelScope.launch {
            delay(400)
            _searchResult.value = UiState(isLoading = true)
            try {
                val results = placesRepository.searchPlaces(query)
                _searchResult.value = UiState(data = results)
            } catch (e: Exception) {
                _searchResult.value = UiState(error = e.message)
            }
        }
    }
}

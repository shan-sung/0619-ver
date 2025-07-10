package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.Attraction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(): ViewModel() {
    private val _savedAttractions = MutableStateFlow<List<Attraction>>(emptyList())
    val savedAttractions: StateFlow<List<Attraction>> = _savedAttractions

    fun addToSaved(attraction: Attraction) {
        _savedAttractions.value = _savedAttractions.value + attraction
    }

    fun removeFromSaved(attraction: Attraction) {
        _savedAttractions.value = _savedAttractions.value.filter { it.id != attraction.id }
    }
}

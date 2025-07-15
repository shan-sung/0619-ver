package com.example.myapplication.viewmodel.saved

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.SavedRepository
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.CurrentUser
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

    fun addToSaved(attraction: Attraction) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                savedRepo.addToSaved(userId, attraction)
                fetchSavedAttractions() // 重新載入收藏清單
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
}

package com.example.myapplication.viewmodel.explore

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.PlacesApiService
import com.example.myapplication.api.SavedRepository
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.CurrentUser
import com.example.myapplication.util.buildPhotoUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val placesApi: PlacesApiService,
    private val savedRepo: SavedRepository
) : ViewModel() {

    private val _recommended = MutableStateFlow<List<Attraction>>(emptyList())
    val recommended: StateFlow<List<Attraction>> = _recommended

    fun fetchRecommendedAttractions(apiKey: String) {
        val userId = CurrentUser.user?.id ?: return
        viewModelScope.launch {
            try {
                val saved = savedRepo.getSavedAttractions(userId)

                // 萃取關鍵字（景點名稱拆字詞）
                val keywords = saved.map { it.name }
                    .flatMap { it.split(Regex("[\\s,，\\-]")) }
                    .filter { it.length >= 2 }
                    .groupingBy { it }.eachCount()
                    .entries.sortedByDescending { it.value }
                    .take(3)
                    .map { it.key }

                val results = mutableListOf<Attraction>()
                for (kw in keywords) {
                    val response = placesApi.getTextSearchResults(query = kw, apiKey = apiKey)
                    results += response.results.map {
                        Attraction(
                            id = it.place_id,
                            name = it.name,
                            city = it.vicinity ?: "未知地點",
                            country = "Taiwan",
                            rating = it.rating ?: 0.0,
                            category = "推薦",
                            imageUrl = it.photos?.firstOrNull()?.photo_reference?.let { ref ->
                                buildPhotoUrl(ref)
                            } ?: ""
                        )
                    }
                }

                val deduplicated = results
                    .distinctBy { it.id }
                    .filterNot { saved.map { s -> s.id }.contains(it.id) }

                _recommended.value = deduplicated

            } catch (e: Exception) {
                Log.e("RecommendationVM", "推薦失敗：${e.message}", e)
            }
        }
    }
}

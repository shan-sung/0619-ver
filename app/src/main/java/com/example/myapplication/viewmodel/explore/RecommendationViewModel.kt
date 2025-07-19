package com.example.myapplication.viewmodel.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.saved.SavedRepository
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.TextSearchPlace
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationViewModel @Inject constructor(
    private val savedRepository: SavedRepository
) : ViewModel() {

    private val _recommendations = MutableStateFlow<List<Attraction>>(emptyList())
    val recommendations: StateFlow<List<Attraction>> = _recommendations

    // 記錄先前推薦過的 ID（避免重複）
    private val previousRecommendations = mutableSetOf<String>()

    fun updateRecommendations(
        savedList: List<Attraction>,
        allAttractions: List<Attraction>,
        textSearchResults: List<TextSearchPlace>? = null
    ) {
        val ignoredTags = setOf("point_of_interest", "establishment", "tourist_attraction")

        // 使用者偏好 tag（去除雜訊）
        val savedTags = savedList
            .flatMap { it.tags ?: emptyList() }
            .filterNot { it in ignoredTags }
            .groupingBy { it }.eachCount()
            .entries.sortedByDescending { it.value }
            .map { it.key }

        val localCandidates = allAttractions.filterNot { it in savedList }

        val textSearchAttractions = textSearchResults?.map {
            Attraction(
                id = it.place_id,
                name = it.name,
                description = it.formatted_address,
                rating = it.rating,
                tags = it.types
            )
        } ?: emptyList()

        // 先合併本地與 text search 結果，移除之前推薦過的景點
        val remaining = (localCandidates + textSearchAttractions)
            .distinctBy { it.id }
            .filterNot { it.id in previousRecommendations }
            .toMutableList()

        // 隨機從使用者偏好 tag 中挑 3 個，加上資料中常見 tag 中隨機挑 2 個
        val randomTags = remaining.flatMap { it.tags ?: emptyList() }
            .filterNot { it in ignoredTags }
            .distinct()
            .shuffled()
            .take(2)

        val topTags = (savedTags.shuffled().take(3) + randomTags).distinct()

        val selected = mutableSetOf<Attraction>()

        for (tag in topTags) {
            val candidates = remaining.filter { it.tags?.contains(tag) == true }
            val picked = candidates.shuffled().take(1)
            selected.addAll(picked)
            remaining.removeAll(picked)
        }

        // 若推薦數不足，補足
        if (selected.size < 5) {
            val filler = remaining.shuffled().take(5 - selected.size)
            selected.addAll(filler)
        }

        val finalResult = selected.toMutableList()

        // 若推薦數不足，從剩下的補足，直到滿 5 筆
        while (finalResult.size < 5 && remaining.isNotEmpty()) {
            finalResult += remaining.removeAt(0)
        }

        // 若還是不足，從已有的推薦中補（防止 UI 空白）
        while (finalResult.size < 5 && selected.isNotEmpty()) {
            finalResult += selected.random()
        }

        _recommendations.value = finalResult.shuffled()
        previousRecommendations.clear()
        previousRecommendations.addAll(finalResult.map { it.id })

    }
    fun shuffleRecommendations(
        savedList: List<Attraction>,
        allAttractions: List<Attraction>
    ) {
        viewModelScope.launch {
            val keywordOptions = listOf("秘境", "浪漫", "文化古蹟", "步道", "親子景點", "熱門景點")
            val randomKeyword = keywordOptions.random()

            val textSearchResults = try {
                savedRepository.getPlacesByKeyword(randomKeyword)
            } catch (e: Exception) {
                emptyList()
            }

            updateRecommendations(savedList, allAttractions, textSearchResults)
        }
    }
}
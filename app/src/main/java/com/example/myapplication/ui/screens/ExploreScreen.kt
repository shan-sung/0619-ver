package com.example.myapplication.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.SearchItem
import com.example.myapplication.data.SearchType
import com.example.myapplication.data.Travel
import com.example.myapplication.ui.components.CustomizableSearchBar
import com.example.myapplication.ui.components.ExploreSection
import com.example.myapplication.ui.components.SearchResultList
import com.example.myapplication.util.getCurrentOrFallbackLocation
import com.example.myapplication.util.handleSearchResultClick
import com.example.myapplication.viewmodel.AttractionsViewModel
import kotlinx.coroutines.delay

/*
ExploreScreen()
├─ CustomizableSearchBar
├─ Featured 旅程：CardRowLib(travels)
└─ Attractions 景點：CardColLib(attractions)
*/

@Composable
fun ExploreScreen(navController: NavController, travels: List<Travel>) {
    var query by rememberSaveable { mutableStateOf("") }

    val queryDebounced by produceState(initialValue = query, key1 = query) {
        delay(300)
        value = query
    }

    val context = LocalContext.current
    val viewModel: AttractionsViewModel = hiltViewModel()
    val attractions by viewModel.attractions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val filteredItems by remember(queryDebounced, attractions, travels) {
        derivedStateOf {
            if (queryDebounced.isBlank()) emptyList()
            else {
                val fromTravels = travels.map { SearchItem(it.title, SearchType.TRAVEL) }
                val fromAttractions = attractions.map { SearchItem(it.name, SearchType.ATTRACTION) }
                (fromTravels + fromAttractions).filter {
                    it.label.contains(queryDebounced, ignoreCase = true)
                }
            }
        }
    }

    // 抓定位
    LaunchedEffect(Unit) {
        val latLng = getCurrentOrFallbackLocation(context)
        Log.d("ExploreScreen", "使用座標：$latLng")
        viewModel.fetchNearbyAttractions(latLng)
    }

    LaunchedEffect(queryDebounced) {
        if (queryDebounced.isNotBlank()) {
            viewModel.searchAttractionsByText(queryDebounced)
        }
    }


    // 主畫面
    Column(modifier = Modifier.fillMaxWidth()) {

        // 搜尋欄
        CustomizableSearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { },
            searchResults = filteredItems,
            onResultClick = { selected ->
                query = selected.label
                handleSearchResultClick(selected, travels, attractions, navController, context)
            },
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        )

        // 搜尋結果列表
        if (queryDebounced.isNotBlank() && filteredItems.isNotEmpty()) {
            SearchResultList(items = filteredItems) { selected ->
                query = selected.label
                handleSearchResultClick(selected, travels, attractions, navController, context)
            }
        }
        ExploreSection(navController = navController, isLoading = isLoading, attractions = attractions) // Featured 與 Attractions 區塊
    }
}
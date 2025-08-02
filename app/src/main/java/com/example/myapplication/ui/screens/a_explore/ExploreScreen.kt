package com.example.myapplication.ui.screens.a_explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.components.placedetaildialog.PlaceDetailDialog
import com.example.myapplication.ui.components.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.viewmodel.explore.AttractionsViewModel
import com.example.myapplication.viewmodel.explore.RecommendationViewModel
import com.example.myapplication.viewmodel.explore.TripsViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun ExploreScreen(
    navController: NavController,
    tripsViewModel: TripsViewModel = hiltViewModel(),
    attractionsViewModel: AttractionsViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel(),
    recommendationViewModel: RecommendationViewModel = hiltViewModel()
) {
    val travels by tripsViewModel.trips.collectAsState()
    val attractions by attractionsViewModel.attractions.collectAsState()
    val savedAttractions by savedViewModel.savedAttractions.collectAsState()
    val recommendations by recommendationViewModel.recommendations.collectAsState()

    val context = LocalContext.current
    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }

    // 初始化資料
    LaunchedEffect(Unit) {
        tripsViewModel.fetchAllTrips()
        attractionsViewModel.fetchNearbyAttractions(context)
        savedViewModel.fetchSavedAttractions()
    }

    LaunchedEffect(savedAttractions, attractions) {
        if (savedAttractions.isNotEmpty() && attractions.isNotEmpty()) {
            recommendationViewModel.updateRecommendations(savedAttractions, attractions)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            if (travels.isNotEmpty()) {
                popularTripsSection(travels, navController)
            }

            if (recommendations.isNotEmpty()) {
                recommendedAttractionsSection(
                    attractions = recommendations,
                    onShuffle = {
                        recommendationViewModel.shuffleRecommendations(savedAttractions, attractions)
                    },
                    context = context,
                    onItemClick = { selectedAttraction = it }
                )
            }

            if (attractions.isNotEmpty()) {
                nearbyAttractionsSection(
                    attractions = attractions,
                    navController = navController,
                    context = context,
                    onItemClick = { selectedAttraction = it }
                )
            }
        }

        selectedAttraction?.let { attraction ->
            PlaceDetailDialog(
                attraction = attraction,
                mode = PlaceActionMode.ADD_TO_FAVORITE,
                onDismiss = { selectedAttraction = null },
                onAddToFavorite = { /* 加入最愛處理邏輯 */ }
            )
        }
    }
}
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
import com.example.myapplication.ui.components.placedetaildialog.PlaceDetailDialog
import com.example.myapplication.ui.components.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.viewmodel.explore.AttractionsViewModel
import com.example.myapplication.viewmodel.explore.TripsViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun ExploreScreen(
    navController: NavController,
    tripsViewModel: TripsViewModel = hiltViewModel(),
    attractionsViewModel: AttractionsViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel(),
) {
    val travels by tripsViewModel.trips.collectAsState()
    val attractions by attractionsViewModel.attractions.collectAsState()
    val context = LocalContext.current
    val attractionDetail = attractionsViewModel.selectedAttractionDetail.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }

    // 初始化資料
    LaunchedEffect(Unit) {
        tripsViewModel.fetchAllTrips()
        attractionsViewModel.fetchNearbyAttractions(context)
        savedViewModel.fetchSavedAttractions()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            if (travels.isNotEmpty()) {
                popularTripsSection(travels, navController)
            }

            if (attractions.isNotEmpty()) {
                nearbyAttractionsSection(
                    attractions = attractions,
                    navController = navController,
                    context = context,
                    // ✅ 點擊時改為載入詳細資料
                    onItemClick = {
                        attractionsViewModel.loadAttractionDetail(it.id)
                        showDialog = true
                    }
                )
            }
        }

        if (showDialog && attractionDetail != null) {
            PlaceDetailDialog(
                attraction = attractionDetail,
                mode = PlaceActionMode.ADD_TO_FAVORITE,
                onDismiss = {
                    showDialog = false
                },
                onAddToFavorite = {
                    savedViewModel.addToSaved(attractionDetail)
                    showDialog = false
                }
            )
        }
    }
}
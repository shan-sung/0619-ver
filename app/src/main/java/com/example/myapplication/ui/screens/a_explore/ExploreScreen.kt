package com.example.myapplication.ui.screens.a_explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.dialogs.placedetaildialog.PlaceDetailDialog
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.viewmodel.explore.AttractionsViewModel
import com.example.myapplication.viewmodel.explore.TripsViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun ExploreScreen(
    navController: NavController,
    tripsViewModel: TripsViewModel = hiltViewModel(),
    attractionsViewModel: AttractionsViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel() // 共用 from saved_graph
) {
    // ⏳ 狀態觀察
    val travels by tripsViewModel.trips.collectAsState()
    val attractions by attractionsViewModel.attractions.collectAsState()
    val selectedAttraction = attractionsViewModel.selectedAttractionDetail.collectAsState().value

    // 📍 其他狀態
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }

    // ⏱️ 初始化
    LaunchedEffect(Unit) {
        tripsViewModel.fetchAllTrips()                 // 來自 trips 模組
        attractionsViewModel.fetchNearbyAttractions(context) // 來自 explore 模組
        savedViewModel.fetchSavedAttractions()         // 來自 saved_graph 共用
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            if (travels.isNotEmpty()) {
                popularTripsSection(
                    travels = travels,
                    navController = navController
                )
            }

            if (attractions.isNotEmpty()) {
                nearbyAttractionsSection(
                    attractions = attractions,
                    navController = navController,
                    context = context,
                    onItemClick = { attraction ->
                        attractionsViewModel.loadAttractionDetail(attraction.id)
                        showDialog = true
                    }
                )
            }
        }

        // 📍 景點詳情 Dialog
        if (showDialog && selectedAttraction != null) {
            PlaceDetailDialog(
                attraction = selectedAttraction,
                mode = PlaceActionMode.ADD_TO_FAVORITE,
                onDismiss = { showDialog = false },
                onAddToFavorite = {
                    savedViewModel.addToSaved(selectedAttraction)
                    showDialog = false
                }
            )
        }
    }
}
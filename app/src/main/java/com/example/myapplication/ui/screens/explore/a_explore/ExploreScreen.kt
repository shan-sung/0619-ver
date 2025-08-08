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
import com.example.myapplication.viewmodel.AttractionsViewModel
import com.example.myapplication.viewmodel.TripsViewModel
import com.example.myapplication.viewmodel.SavedViewModel

@Composable
fun ExploreScreen(
    navController: NavController,
    tripsViewModel: TripsViewModel = hiltViewModel(),
    attractionsViewModel: AttractionsViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel()
) {
    val tripsUiState by tripsViewModel.uiState.collectAsState()
    val attractionsUiState by attractionsViewModel.uiState.collectAsState()
    val selectedAttraction = attractionsViewModel.selectedAttractionDetail.collectAsState().value

    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        tripsViewModel.fetchAllTrips()
        attractionsViewModel.loadNearbyAttractions(context)
        savedViewModel.loadSavedAttractions()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            // ✅ 正確使用 tripsUiState.data
            if (tripsUiState.data.orEmpty().isNotEmpty()) {
                popularTripsSection(
                    travels = tripsUiState.data.orEmpty(),
                    navController = navController
                )
            }

            if (attractionsUiState.data.orEmpty().isNotEmpty()) {
                nearbyAttractionsSection(
                    attractions = attractionsUiState.data.orEmpty(),
                    navController = navController,
                    context = context,
                    onItemClick = { attraction ->
                        attractionsViewModel.loadAttractionDetail(attraction.id)
                        showDialog = true
                    }
                )
            }
        }

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

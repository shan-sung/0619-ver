package com.example.myapplication.ui.screens.c_saved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.components.placedetaildialog.PlaceDetailDialog
import com.example.myapplication.ui.components.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.element.AttractionList
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun SavedScreen(
    navController: NavController,
    viewModel: SavedViewModel = hiltViewModel(),
    onSelect: (Attraction) -> Unit,
    onAddToItinerary: (Attraction) -> Unit
) {
    val savedList by viewModel.savedAttractions.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val attractionDetail = viewModel.selectedAttractionDetail.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        AttractionList(
            attractions = savedList,
            onClick = {
                viewModel.loadAttractionDetail(it.id)
                showDialog = true
            },
            onRemove = { viewModel.removeFromSaved(it) }
        )
    }

    // 🔍 顯示詳細 Dialog
    if (showDialog && attractionDetail != null) {
        PlaceDetailDialog(
            attraction = attractionDetail,
            mode = PlaceActionMode.REMOVE_FROM_FAVORITE,
            onDismiss = { showDialog = false },
            onAddToItinerary = { onAddToItinerary(attractionDetail) }
        )
    }
}
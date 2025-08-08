package com.example.myapplication.ui.screens.c_saved

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.components.dialogs.placedetaildialog.PlaceDetailDialog
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.element.AttractionList
import com.example.myapplication.viewmodel.SavedViewModel

@Composable
fun SavedScreen(
    navController: NavController,
    viewModel: SavedViewModel = hiltViewModel(),
    onSelect: (Attraction) -> Unit,
    onAddToItinerary: (Attraction) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val savedList = uiState.data.orEmpty()
    val isLoading = uiState.isLoading
    val errorMessage = uiState.error

    val attractionDetail = viewModel.selectedAttractionDetail.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (!errorMessage.isNullOrEmpty()) {
            Text(
                text = "錯誤：$errorMessage",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            AttractionList(
                attractions = savedList,
                onClick = {
                    viewModel.loadAttractionDetail(it.id)
                    showDialog = true
                },
                onRemove = { viewModel.removeFromSaved(it) }
            )
        }
    }

    // 🔍 詳細資訊 Dialog
    if (showDialog && attractionDetail != null) {
        PlaceDetailDialog(
            attraction = attractionDetail,
            mode = PlaceActionMode.REMOVE_FROM_FAVORITE,
            onDismiss = { showDialog = false },
            onAddToItinerary = { onAddToItinerary(attractionDetail) },
            onRemoveFromFavorite = {
                viewModel.removeFromSaved(attractionDetail)
                showDialog = false
            }
        )
    }
}
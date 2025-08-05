package com.example.myapplication.ui.screens.b_myplans.e_addPlace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.components.dialogs.placedetaildialog.PlaceDetailDialog
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.element.AttractionList
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.element.SearchBar
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.element.TabRowSection
import com.example.myapplication.viewmodel.ForYouViewModel
import com.example.myapplication.viewmodel.SavedViewModel

@Composable
fun SelectFromMapScreen(
    navController: NavController,
    travelId: String,
    savedViewModel: SavedViewModel = hiltViewModel(),
    forYouViewModel: ForYouViewModel = hiltViewModel(),
    onSelect: (Attraction) -> Unit,
    onAddToItinerary: (Attraction) -> Unit
) {
    val tabs = listOf("For you", "Saved")
    var selectedTab by remember { mutableIntStateOf(0) }

    val forYouState = forYouViewModel.recommendState.collectAsState().value
    val savedState = savedViewModel.uiState.collectAsState().value
    val currentList = if (selectedTab == 0) forYouState.data.orEmpty() else savedState.data.orEmpty()

    val attractionDetail =
        if (selectedTab == 0) forYouViewModel.selectedAttraction.collectAsState().value
        else savedViewModel.selectedAttractionDetail.collectAsState().value

    var showDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(navController = navController, travelId = travelId)

        TabRowSection(
            tabs = tabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        AttractionList(
            attractions = currentList,
            onClick = {
                if (selectedTab == 0) {
                    forYouViewModel.loadAttractionDetail(it.id)
                } else {
                    savedViewModel.loadAttractionDetail(it.id)
                }
                showDialog = true
            },
            onRemove = {
                if (selectedTab == 1) savedViewModel.removeFromSaved(it)
            }
        )
    }

    if (showDialog && attractionDetail != null) {
        PlaceDetailDialog(
            attraction = attractionDetail,
            mode = PlaceActionMode.ADD_TO_ITINERARY,
            onDismiss = { showDialog = false },
            onAddToItinerary = {
                onAddToItinerary(attractionDetail)
                showDialog = false
            }
        )
    }
}
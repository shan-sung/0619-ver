package com.example.myapplication.ui.screens.b_myplans.e_addPlace

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.dialogs.placedetaildialog.PlaceDetailDialog
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.element.PlaceItem
import com.example.myapplication.viewmodel.AttractionsViewModel
import com.example.myapplication.viewmodel.SearchViewModel

@Composable
fun SearchMapsWrapper(
    navController: NavController,
    travelId: String,
    searchViewModel: SearchViewModel = hiltViewModel(),
    attractionsViewModel: AttractionsViewModel = hiltViewModel()
) {
    val uiState = searchViewModel.uiState.collectAsState().value
    val searchResults = uiState.data.orEmpty()

    val recentSearchIds = rememberSaveable { mutableStateOf(listOf<String>()) }

    val recentSearches = remember(searchResults, recentSearchIds.value) {
        searchResults.filter { it.id in recentSearchIds.value }
    }

    val selectedAttraction = attractionsViewModel.selectedAttractionDetail.collectAsState().value

    SearchMapsScreen(
        navController = navController,
        searchResults = searchResults,
        recentSearches = recentSearches,
        onSearchQueryChanged = { query -> searchViewModel.debouncedSearch(query) },
        onSelect = { selected ->
            if (selected.id !in recentSearchIds.value) {
                recentSearchIds.value = recentSearchIds.value + selected.id
            }
            // ✅ 補充完整詳細資料
            attractionsViewModel.loadAttractionDetail(selected.id)
        },
        selectedAttraction = selectedAttraction,
        onDismissDialog = { attractionsViewModel.clearSelectedAttraction() },
        onAddToItinerary = { attraction ->
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.set("selected_attraction", attraction)

            navController.navigate(Routes.MyPlans.addScheduleRoute(travelId)) {
                popUpTo(Routes.MyPlans.SEARCH) { inclusive = true }
            }
        }
    )
}


@Composable
fun SearchMapsScreen(
    navController: NavController,
    searchResults: List<Attraction>,
    recentSearches: List<Attraction>,
    onSearchQueryChanged: (String) -> Unit,
    onSelect: (Attraction) -> Unit,
    selectedAttraction: Attraction?,
    onDismissDialog: () -> Unit,
    onAddToItinerary: (Attraction) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(searchText) {
        if (searchText.isNotBlank()) onSearchQueryChanged(searchText)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search Place") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchText.isNotBlank()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        if (searchText.isBlank()) {
            Text(
                "Recent",
                modifier = Modifier.padding(start = 16.dp, top = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn {
                items(recentSearches.reversed()) { attraction ->
                    PlaceItem(
                        attraction = attraction,
                        onClick = { onSelect(attraction) },
                        onRemove = {}
                    )
                }
            }
        } else {
            LazyColumn {
                items(searchResults) { attraction ->
                    PlaceItem(attraction, onClick = { onSelect(attraction) }, onRemove = {})
                }
            }
        }
    }

    if (selectedAttraction != null) {
        PlaceDetailDialog(
            attraction = selectedAttraction,
            mode = PlaceActionMode.ADD_TO_ITINERARY,
            onDismiss = onDismissDialog,
            onAddToItinerary = {
                onAddToItinerary(selectedAttraction)
                onDismissDialog()
            }
        )
    }
}

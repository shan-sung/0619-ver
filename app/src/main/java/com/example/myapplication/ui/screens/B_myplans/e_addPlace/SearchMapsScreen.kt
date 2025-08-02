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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.element.SavedPlaceItem
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun SearchMapsWrapper(
    navController: NavController,
    viewModel: SavedViewModel
) {
    val saved by viewModel.savedAttractions.collectAsState()
    val forYou by viewModel.forYouAttractions.collectAsState()

    val allAttractions = saved + forYou
    val recentSearches = remember { mutableStateListOf<Attraction>() }

    SearchMapsScreen(
        navController = navController,
        allAttractions = allAttractions,
        recentSearches = recentSearches,
        onSelect = { selectedAttraction ->
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("selected_attraction", selectedAttraction)
            navController.popBackStack()
        }
    )
}

@Composable
fun SearchMapsScreen(
    navController: NavController,
    allAttractions: List<Attraction>,
    onSelect: (Attraction) -> Unit,
    recentSearches: SnapshotStateList<Attraction>
) {
    var searchText by remember { mutableStateOf("") }

    val filtered = remember(searchText) {
        allAttractions.filter {
            it.name.contains(searchText, true) || (it.address?.contains(searchText, true) ?: false)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 🔍 搜尋欄
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
                    SavedPlaceItem(
                        attraction = attraction,
                        onClick = {
                            onSelect(attraction)
                            navController.popBackStack()
                        },
                        onRemove = {}
                    )
                }
            }
        } else {
            LazyColumn {
                items(filtered) { attraction ->
                    SavedPlaceItem(
                        attraction = attraction,
                        onClick = {
                            if (!recentSearches.contains(attraction)) {
                                recentSearches.add(attraction)
                            }
                            onSelect(attraction)
                            navController.popBackStack()
                        },
                        onRemove = {}
                    )
                }
            }
        }
    }
}
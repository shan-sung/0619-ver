package com.example.myapplication.ui.screens.myplans.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Attraction
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun SelectFromSavedListScreen(
    navController: NavController,
    viewModel: SavedViewModel = hiltViewModel(),
    onSelect: (Attraction) -> Unit
) {
    val tabs = listOf("For you", "Saved")
    var selectedTab by remember { mutableIntStateOf(2) }

    val savedList by viewModel.savedAttractions.collectAsState()
    val forYouList by viewModel.forYouAttractions.collectAsState()
    val currentList = if (selectedTab == 0) forYouList else savedList

    var showDialog by remember { mutableStateOf(false) }
    val attractionDetail = viewModel.selectedAttractionDetail.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(navController)
        TabRowSection(
            tabs = tabs,
            selectedIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        AttractionList(
            attractions = currentList,
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
            onDismiss = { showDialog = false },
            onAddToItinerary = {
               // TODO
            }
        )
    }
}


@Composable
private fun SearchBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Routes.MyPlans.SEARCH) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "Search Place",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun TabRowSection(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            )
        }
    }
}

@Composable
private fun AttractionList(
    attractions: List<Attraction>,
    onClick: (Attraction) -> Unit,
    onRemove: (Attraction) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(attractions) { attraction ->
            SavedPlaceItem(
                attraction = attraction,
                onClick = { onClick(attraction) },
                onRemove = onRemove
            )
        }
    }
}
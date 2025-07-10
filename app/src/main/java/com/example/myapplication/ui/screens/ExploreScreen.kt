package com.example.myapplication.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.screens.explore.ExploreSection
import com.example.myapplication.util.getCurrentOrFallbackLocation
import com.example.myapplication.viewmodel.AttractionsViewModel
import com.example.myapplication.viewmodel.SavedViewModel
import com.example.myapplication.viewmodel.TripsViewModel
import kotlinx.coroutines.launch

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    savedViewModel: SavedViewModel = hiltViewModel() // ✅ 在這統一建立
) {
    val context = LocalContext.current
    val viewModel: AttractionsViewModel = hiltViewModel()
    val tripsViewModel: TripsViewModel = hiltViewModel()

    val attractions by viewModel.attractions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val allTrips by tripsViewModel.trips.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val refreshNearbyAttractions = remember {
        suspend {
            val latLng = getCurrentOrFallbackLocation(context)
            Log.d("ExploreScreen", "使用座標：$latLng")
            viewModel.fetchNearbyAttractions(latLng)
        }
    }

    LaunchedEffect(Unit) {
        refreshNearbyAttractions()
        tripsViewModel.fetchAllTrips()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ExploreSection(
            navController = navController,
            isLoading = isLoading,
            attractions = attractions,
            onRefresh = {
                coroutineScope.launch {
                    refreshNearbyAttractions()
                }
            },
            travels = allTrips,
            savedViewModel = savedViewModel // ✅ 傳遞共用 ViewModel
        )

    }
}

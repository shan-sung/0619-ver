package com.example.myapplication.ui.screens.b_myplans._comp

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.TripItem
import com.example.myapplication.viewmodel.TripsViewModel

@Composable
fun CreatedTripsScreen(
    navController: NavController,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val createdTrips by viewModel.myCreatedTrips.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllTrips()
    }

    when {
        uiState.isLoading -> {
            CircularProgressIndicator()
        }
        uiState.error != null -> {
            Text("錯誤：${uiState.error}")
        }
        else -> {
            LazyColumn {
                items(createdTrips) { trip ->
                    TripItem(travel = trip, navController = navController)
                }
            }
        }
    }
}


@Composable
fun ParticipatingTripsScreen(
    navController: NavController,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val joinedTrips by viewModel.myJoinedTrips.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllTrips()
    }

    when {
        uiState.isLoading -> {
            CircularProgressIndicator()
        }
        uiState.error != null -> {
            Text("錯誤：${uiState.error}")
        }
        else -> {
            LazyColumn {
                items(joinedTrips) { trip ->
                    TripItem(travel = trip, navController = navController)
                }
            }
        }
    }
}

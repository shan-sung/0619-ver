package com.example.myapplication.ui.screens.trips

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.CardColLibForTravels
import com.example.myapplication.viewmodel.TripsViewModel

@Composable
fun ParticipatingTripsScreen(
    navController: NavController,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val joinedTrips by viewModel.joinedTrips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllTrips()
    }

    when {
        isLoading -> {
            CircularProgressIndicator()
        }
        errorMessage != null -> {
            Text("錯誤：$errorMessage")
        }
        else -> {
            CardColLibForTravels(navController = navController, travels = joinedTrips)
        }
    }
}
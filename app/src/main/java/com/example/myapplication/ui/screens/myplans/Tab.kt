package com.example.myapplication.ui.screens.myplans

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
import com.example.myapplication.ui.components.InfoCardVertical
import com.example.myapplication.ui.components.toInfoCardData
import com.example.myapplication.viewmodel.explore.TripsViewModel

@Composable
fun CreatedTripsScreen(
    navController: NavController,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val createdTrips by viewModel.myCreatedTrips.collectAsState()
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
            LazyColumn {
                items(createdTrips) { trip ->
                    InfoCardVertical(data = trip.toInfoCardData(navController))
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
    val joinedTrips by viewModel.myJoinedTrips.collectAsState()
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
            LazyColumn {
                items(joinedTrips) { trip ->
                    InfoCardVertical(data = trip.toInfoCardData(navController))
                }
            }
        }
    }
}
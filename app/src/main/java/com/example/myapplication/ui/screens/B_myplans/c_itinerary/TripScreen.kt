package com.example.myapplication.ui.screens.b_myplans.c_itinerary

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.content.TripContent
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel

@Composable
fun TripScreen(
    navController: NavController,
    travelId: String,
    scrollToDay: Int?, // 👈 直接當參數接收
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val travel by viewModel.travel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val savedStateHandle = currentBackStackEntry?.savedStateHandle

    val selectedAttraction = savedStateHandle
        ?.getStateFlow<Attraction?>("selected_attraction", null)
        ?.collectAsState()?.value

    LaunchedEffect(travelId) {
        viewModel.fetchTravelById(travelId)
    }

    when {
        isLoading -> {
            CircularProgressIndicator()
        }

        travel != null -> {
            TripContent(
                navController = navController,
                travel = travel!!,
                selectedAttraction = selectedAttraction,
                scrollToDay = scrollToDay,
                onScheduleAdded = {
                    // TODO
                }
            )
        }

        else -> {
            Text("找不到行程")
        }
    }
}

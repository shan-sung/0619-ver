package com.example.myapplication.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.trips.CreatedTripsScreen
import com.example.myapplication.ui.screens.trips.ParticipatingTripsScreen
import com.example.myapplication.ui.screens.trips.TripTab

fun NavGraphBuilder.tripTabNav(parentNavController: NavHostController) {
    composable(TripTab.CREATED.route) {
        CreatedTripsScreen(navController = parentNavController)
    }
    composable(TripTab.PARTICIPATING.route) {
        ParticipatingTripsScreen(navController = parentNavController)
    }
}

@Composable
fun TripNavHost(
    navController: NavHostController,
    startDestination: TripTab,
    parentNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier // 記得套用進來
    ) {
        tripTabNav(parentNavController)
    }
}

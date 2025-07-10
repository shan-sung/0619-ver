package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.trips.TripsScreen

fun NavGraphBuilder.tripNav(parentNavController: NavHostController) {
    composable("trips") {
        TripsScreen(navController = parentNavController)
    }
}

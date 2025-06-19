package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.data.mockTravels
import com.example.myapplication.ui.screens.ExploreScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "explore"
    ) {
        composable("explore") {
            ExploreScreen(navController = navController, travels = mockTravels)
        }
    }
}
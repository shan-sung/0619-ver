package com.example.myapplication.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.ChatRoomScreen
import com.example.myapplication.ui.screens.CreateTripWizardScreen
import com.example.myapplication.ui.screens.ExploreScreen
import com.example.myapplication.ui.screens.TripScreen
import com.example.myapplication.ui.screens.TripsScreen
import com.example.myapplication.ui.screens.trips.CreatedTripsScreen
import com.example.myapplication.ui.screens.trips.ParticipatingTripsScreen
import com.example.myapplication.ui.screens.trips.TripTab

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = "explore"
    ) {
        composable("explore") {
            ExploreScreen(
                navController = navController,
                modifier = modifier
            )
        }
        composable("chat") {
            ChatRoomScreen(modifier = modifier)
        }
        composable("trips") {
            TripsScreen(navController = navController, modifier = modifier)
        }
        composable("create") {
            CreateTripWizardScreen(modifier = modifier, navController)
        }
        composable(
            route = "travel_detail/{travelId}",
            arguments = listOf(navArgument("travelId") { type = NavType.StringType })
        ) { backStackEntry ->
            val travelId = backStackEntry.arguments?.getString("travelId") ?: ""
            TripScreen(navController = navController, travelId = travelId)
        }


    }
}

@Composable
fun TripNavHost(navController: NavHostController, startDestination: TripTab) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(TripTab.CREATED.route) {
            CreatedTripsScreen(navController = navController)
        }
        composable(TripTab.PARTICIPATING.route) {
            ParticipatingTripsScreen(navController = navController)
        }
    }
}

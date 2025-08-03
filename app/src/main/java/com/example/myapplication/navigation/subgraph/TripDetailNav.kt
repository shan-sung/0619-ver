package com.example.myapplication.navigation.subgraph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.TripScreen

fun NavGraphBuilder.tripDetailNavGraph(navController: NavController) {
    composable(
        route = Routes.MyPlans.DETAIL,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("id") ?: return@composable
        TripScreen(navController = navController, travelId = travelId)
    }
}
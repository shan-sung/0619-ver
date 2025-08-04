package com.example.myapplication.navigation.subgraph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.TripScreen

fun NavGraphBuilder.tripDetailNavGraph(navController: NavController) {
    composable(
        route = "my_plans/detail/{id}?scrollToDay={scrollToDay}",
        arguments = listOf(
            navArgument("id") { type = NavType.StringType },
            navArgument("scrollToDay") {
                type = NavType.IntType
                defaultValue = -1
            }
        )
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("id") ?: return@composable
        val scrollToDay = backStackEntry.arguments?.getInt("scrollToDay")?.takeIf { it >= 1 }

        TripScreen(
            navController = navController,
            travelId = travelId,
            scrollToDay = scrollToDay
        )
    }
}

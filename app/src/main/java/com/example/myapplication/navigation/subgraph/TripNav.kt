package com.example.myapplication.navigation.subgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.a_entry.TripsScreen

fun NavGraphBuilder.tripNavGraph(navController: NavHostController) {
    composable(Routes.App.MY_PLANS) {
        TripsScreen(navController = navController)
    }
}
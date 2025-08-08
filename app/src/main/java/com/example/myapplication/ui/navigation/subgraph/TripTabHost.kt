package com.example.myapplication.navigation.subgraph

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans._comp.CreatedTripsScreen
import com.example.myapplication.ui.screens.b_myplans._comp.ParticipatingTripsScreen
import com.example.myapplication.ui.screens.b_myplans._comp.TripTab

@Composable
fun TripTabHost(
    navController: NavHostController,
    startDestination: TripTab,
    parentNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(Routes.MyPlans.Tab.CREATED) {
            CreatedTripsScreen(navController = parentNavController)
        }
        composable(Routes.MyPlans.Tab.PARTICIPATING) {
            ParticipatingTripsScreen(navController = parentNavController)
        }
    }
}
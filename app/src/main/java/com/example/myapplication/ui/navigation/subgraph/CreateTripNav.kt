package com.example.myapplication.navigation.subgraph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.b_prev.CreateTripWizardScreen

fun NavGraphBuilder.createTripNavGraph(navController: NavController) {
    composable(Routes.MyPlans.CREATE) {
        CreateTripWizardScreen(navController = navController)
    }
}
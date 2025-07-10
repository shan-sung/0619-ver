package com.example.myapplication.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.creation.CreateTripWizardScreen

fun NavGraphBuilder.createNav(navController: NavController) {
    composable("create") {
        CreateTripWizardScreen(navController = navController)
    }
}

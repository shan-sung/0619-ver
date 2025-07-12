package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.explore.AttractionsScreen

fun NavGraphBuilder.attractionNav(navController: NavHostController) {
    composable("attraction") {
        AttractionsScreen(navController)
    }
}



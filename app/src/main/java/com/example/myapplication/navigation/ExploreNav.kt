package com.example.myapplication.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.ExploreScreen

fun NavGraphBuilder.exploreNav(navController: NavController) {
    composable("explore") {
        ExploreScreen(
            navController = navController
        )
    }
}

package com.example.myapplication.navigation.details

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.explore.AttractionsScreen
import com.example.myapplication.ui.screens.explore.FeaturedScreen

fun NavGraphBuilder.featuredNav(navController: NavHostController) {
    composable(Routes.Explore.Featured.MAIN) {
        FeaturedScreen(navController = navController)
    }
}

fun NavGraphBuilder.attractionNav(navController: NavHostController) {
    composable(Routes.Explore.Attraction.MAIN) {
        AttractionsScreen(navController)
    }
}
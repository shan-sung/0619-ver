package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.explore.FeaturedScreen

fun NavGraphBuilder.featuredNav(navController: NavHostController) {
    composable("featured") {
        FeaturedScreen(navController = navController)
    }
}


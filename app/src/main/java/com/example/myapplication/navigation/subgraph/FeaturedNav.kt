package com.example.myapplication.navigation.subgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.a_explore.FeaturedScreen

fun NavGraphBuilder.featuredNav(navController: NavHostController) {
    composable(Routes.Explore.Featured.MAIN) {
        FeaturedScreen(navController = navController)
    }
}
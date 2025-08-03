package com.example.myapplication.navigation.subgraph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.a_explore.ExploreScreen
import com.example.myapplication.ui.screens.a_explore.FeaturedScreen

fun NavGraphBuilder.exploreNavGraph(navController: NavController) {
    composable(Routes.Explore.MAIN) {
        ExploreScreen(navController = navController)
    }
}


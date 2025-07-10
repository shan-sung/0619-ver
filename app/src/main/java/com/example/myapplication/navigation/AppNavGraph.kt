package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "explore",
        modifier = modifier,
        route = "main"
    ) {
        exploreNav(navController)
        tripNav(navController)
        chatNav()
        createNav(navController)
        tripDetailNav(navController)
        savedNav(navController)
    }
}
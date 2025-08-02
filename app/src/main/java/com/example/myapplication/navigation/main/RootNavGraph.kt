package com.example.myapplication.navigation.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.bar.MainScreen
import com.example.myapplication.ui.screens._auth.LoginScreen
@Composable
fun RootNavGraph() {
    val navController = rememberNavController()
    val startDestination = if (CurrentUser.isLoggedIn()) Routes.Root.MAIN else Routes.Root.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authNavGraph(navController)
        mainNavGraph(navController)
    }
}

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    composable(Routes.Root.LOGIN) {
        LoginScreen(navController = navController)
    }
}

fun NavGraphBuilder.mainNavGraph(navController: NavHostController) {
    composable(Routes.Root.MAIN) {
        MainScreen()
    }
}
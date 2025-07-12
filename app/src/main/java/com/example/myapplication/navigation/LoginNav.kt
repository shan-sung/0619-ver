package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.LoginScreen

fun NavGraphBuilder.authNav(navController: NavHostController) {
    composable("login") {
        LoginScreen(navController = navController)
    }
}

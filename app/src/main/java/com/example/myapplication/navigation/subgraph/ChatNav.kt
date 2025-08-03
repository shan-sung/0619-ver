package com.example.myapplication.navigation.subgraph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.d_features.ChatRoomScreen

fun NavGraphBuilder.chatNavGraph() {
    composable(
        route = Routes.MyPlans.CHAT,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("id") ?: return@composable
        ChatRoomScreen(tripId = tripId)
    }
}
package com.example.myapplication.navigation.subgraph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.d_friend.FriendScreen

fun NavGraphBuilder.friendNavGraph(navController: NavController, refreshKey: Int) {
    composable(Routes.Friend.MAIN) {
        FriendScreen(navController = navController, refreshKey = refreshKey)
    }
}
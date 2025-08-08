package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.navigation.subgraph.chatNavGraph
import com.example.myapplication.navigation.subgraph.createTripNavGraph
import com.example.myapplication.navigation.subgraph.exploreNavGraph
import com.example.myapplication.navigation.subgraph.friendNavGraph
import com.example.myapplication.navigation.subgraph.profileNavGraph
import com.example.myapplication.navigation.subgraph.savedNavGraph
import com.example.myapplication.navigation.subgraph.selectFromMapNavGraph
import com.example.myapplication.navigation.subgraph.tripDetailNavGraph
import com.example.myapplication.navigation.subgraph.tripNavGraph

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier, refreshKey: Int) {
    NavHost(
        navController = navController,
        startDestination = Routes.AppTabs.EXPLORE,
        modifier = modifier
    ) {
        navigation(
            startDestination = Routes.Explore.MAIN,
            route = Routes.Saved.GRAPH // e.g., "saved_graph"
        ) {
            exploreNavGraph(navController)
            savedNavGraph(navController)
        }

        // 其他功能獨立存在
        tripNavGraph(navController)
        chatNavGraph()
        profileNavGraph(navController)
        createTripNavGraph(navController)
        tripDetailNavGraph(navController)
        selectFromMapNavGraph(navController)
        friendNavGraph(navController, refreshKey)
    }
}

package com.example.myapplication.navigation.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.myapplication.navigation.details.chatNav
import com.example.myapplication.navigation.details.createNav
import com.example.myapplication.navigation.details.featuredNav
import com.example.myapplication.navigation.details.previewNav
import com.example.myapplication.navigation.details.selectFromMapNav
import com.example.myapplication.navigation.details.tripDetailNav
import com.example.myapplication.navigation.routes.Routes

@Composable
fun AppNavGraph(navController: NavHostController, modifier: Modifier = Modifier, refreshKey: Int) {
    NavHost(
        navController = navController,
        startDestination = Routes.App.EXPLORE,
        modifier = modifier
    ) {
        exploreNav(navController)
        tripNav(navController)
        chatNav()
        profileNav(navController)
        createNav(navController)
        tripDetailNav(navController)
        savedNav(navController)
        featuredNav(navController)
        selectFromMapNav(navController)
        previewNav(navController)
        friendNav(navController, refreshKey)
    }
}

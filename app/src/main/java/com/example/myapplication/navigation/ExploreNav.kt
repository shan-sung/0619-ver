package com.example.myapplication.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.explore.ExploreScreen
import com.example.myapplication.viewmodel.SavedViewModel

fun NavGraphBuilder.exploreNav(navController: NavController) {
    composable("explore") {
        val savedViewModel: SavedViewModel = hiltViewModel()

        ExploreScreen(
            navController = navController,
            savedViewModel = savedViewModel
        )
    }
}


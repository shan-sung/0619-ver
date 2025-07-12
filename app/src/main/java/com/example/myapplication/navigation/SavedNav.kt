package com.example.myapplication.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.saved.SavedScreen
import com.example.myapplication.viewmodel.SavedViewModel

fun NavGraphBuilder.savedNav(navController: NavController) {
    composable("saved") {
        val savedViewModel: SavedViewModel = hiltViewModel()

        SavedScreen(
            savedViewModel = savedViewModel,
            onItemClick = { /* show detail */ }
        )
    }
}

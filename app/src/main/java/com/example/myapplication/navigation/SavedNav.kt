package com.example.myapplication.navigation

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.saved.SavedScreen
import com.example.myapplication.viewmodel.SavedViewModel

fun NavGraphBuilder.savedNav(navController: NavController) {
    composable("saved") {
        val parentEntry = remember(navController.currentBackStackEntry) {
            navController.getBackStackEntry("main")
        }

        val savedViewModel = hiltViewModel<SavedViewModel>(parentEntry)

        SavedScreen(
            savedViewModel = savedViewModel,
            onItemClick = { /* show detail */ }
        )
    }
}

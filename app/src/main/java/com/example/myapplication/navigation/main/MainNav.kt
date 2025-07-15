package com.example.myapplication.navigation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.explore.ExploreScreen
import com.example.myapplication.ui.screens.myplans.TripsScreen
import com.example.myapplication.ui.screens.profile.ProfileScreen
import com.example.myapplication.ui.screens.saved.SavedScreen
import com.example.myapplication.viewmodel.profile.ProfileViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel

fun NavGraphBuilder.exploreNav(navController: NavController) {
    composable(Routes.Explore.MAIN) {
        val savedViewModel: SavedViewModel = hiltViewModel()
        ExploreScreen(navController = navController, savedViewModel = savedViewModel)
    }
}

fun NavGraphBuilder.tripNav(navController: NavHostController) {
    composable(Routes.App.MY_PLANS) {
        TripsScreen(navController = navController)
    }
}

fun NavGraphBuilder.savedNav(navController: NavController) {
    composable(Routes.App.SAVED) {
        val savedViewModel: SavedViewModel = hiltViewModel()
        SavedScreen(
            savedViewModel = savedViewModel,
            onItemClick = { /* TODO: Navigate to Saved Detail */ }
        )
    }
}

fun NavGraphBuilder.profileNav(navController: NavController) {
    composable(route = Routes.App.PROFILE) {
        val viewModel: ProfileViewModel = hiltViewModel()
        val userState = viewModel.user.collectAsState()

        userState.value?.let { user ->
            ProfileScreen(user = user)
        } ?: run {
            CircularProgressIndicator(modifier = Modifier.fillMaxSize())
        }
    }
}
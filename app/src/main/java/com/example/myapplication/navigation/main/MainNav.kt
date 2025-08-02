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
import com.example.myapplication.ui.screens.a_explore.ExploreScreen
import com.example.myapplication.ui.screens.b_myplans.a_entry.TripsScreen
import com.example.myapplication.ui.screens.d_friend.FriendScreen
import com.example.myapplication.ui.screens.e_profile.ProfileScreen
import com.example.myapplication.ui.screens.c_saved.SavedScreen
import com.example.myapplication.viewmodel.profile.ProfileViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel

fun NavGraphBuilder.exploreNav(navController: NavController) {
    composable(Routes.Explore.MAIN) {
        ExploreScreen(navController = navController)
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
            navController = navController,
            viewModel = savedViewModel,
            onSelect = { attraction ->
                // 如果你未來要加功能，可以在這裡處理點選
            },
            onAddToItinerary = { attraction ->
                // 例如導向新增排程頁面，或傳給 ViewModel 加入行程
            }
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

fun NavGraphBuilder.friendNav(navController: NavController, refreshKey: Int) {
    composable(Routes.App.FRIEND) {
        FriendScreen(navController = navController, refreshKey = refreshKey)
    }
}
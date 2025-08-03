package com.example.myapplication.navigation.subgraph

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.e_profile.ProfileScreen
import com.example.myapplication.viewmodel.profile.ProfileViewModel

fun NavGraphBuilder.profileNavGraph(navController: NavController) {
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
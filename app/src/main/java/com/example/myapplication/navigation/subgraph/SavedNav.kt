package com.example.myapplication.navigation.subgraph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.c_saved.SavedScreen
import com.example.myapplication.viewmodel.saved.SavedViewModel

fun NavGraphBuilder.savedNavGraph(navController: NavController) {
    composable(Routes.Saved.MAIN) {
        val savedViewModel: SavedViewModel = hiltViewModel()

        SavedScreen(
            navController = navController,
            viewModel = savedViewModel,
            onSelect = { attraction ->
                // 可加入跳轉或選擇處理
            },
            onAddToItinerary = { attraction ->
                // 可加入行程草稿等處理
            }
        )
    }
}
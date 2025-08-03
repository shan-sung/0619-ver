package com.example.myapplication.navigation.subgraph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SearchMapsWrapper
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SelectFromMapScreen
import com.example.myapplication.viewmodel.ForYouViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel

fun NavGraphBuilder.selectFromMapNavGraph(navController: NavController) {
    composable(Routes.MyPlans.SELECT_FROM_SAVED) {
        SelectFromMapScreen(
            navController = navController,
            onSelect = { selectedAttraction ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_attraction", selectedAttraction)
                navController.popBackStack()
            },
            onAddToItinerary = {
                // 可加入行程草稿
            }
        )
    }

    composable(Routes.MyPlans.SEARCH) {
        val savedViewModel: SavedViewModel = hiltViewModel()
        val forYouViewModel: ForYouViewModel = hiltViewModel()

        SearchMapsWrapper(
            navController = navController,
            savedViewModel = savedViewModel,
            forYouViewModel = forYouViewModel
        )
    }
}
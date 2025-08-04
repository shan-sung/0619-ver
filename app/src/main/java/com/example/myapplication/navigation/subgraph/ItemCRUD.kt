package com.example.myapplication.navigation.subgraph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.AddScheduleScreen
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
            onAddToItinerary = { selectedAttraction ->
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_attraction", selectedAttraction)
                navController.navigate(Routes.MyPlans.ADD_SCHEDULE)
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

fun NavGraphBuilder.addScheduleNavGraph(navController: NavController) {
    composable(Routes.MyPlans.ADD_SCHEDULE) { backStackEntry ->
        val attraction = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<Attraction>("selected_attraction")

        AddScheduleScreen(
            navController = navController,
            attraction = attraction
        )
    }
}

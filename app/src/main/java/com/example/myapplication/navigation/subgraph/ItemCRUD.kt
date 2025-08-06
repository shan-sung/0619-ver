package com.example.myapplication.navigation.subgraph

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SearchMapsWrapper
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SelectFromMapScreen
import com.example.myapplication.viewmodel.AttractionsViewModel
import com.example.myapplication.viewmodel.SearchViewModel

fun NavGraphBuilder.selectFromMapNavGraph(navController: NavController) {
    composable(
        Routes.MyPlans.SELECT_FROM_SAVED_WITH_ID,
        arguments = listOf(navArgument("travelId") { type = NavType.StringType })
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("travelId") ?: ""
        SelectFromMapScreen(
            navController = navController,
            travelId = travelId
        )
    }

    composable(
        route = Routes.MyPlans.SEARCH + "/{travelId}",
        arguments = listOf(navArgument("travelId") { type = NavType.StringType })
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("travelId") ?: return@composable
        val searchViewModel: SearchViewModel = hiltViewModel()
        val attractionsViewModel: AttractionsViewModel = hiltViewModel()

        SearchMapsWrapper(
            navController = navController,
            travelId = travelId,
            searchViewModel = searchViewModel,
            attractionsViewModel = attractionsViewModel
        )
    }
}
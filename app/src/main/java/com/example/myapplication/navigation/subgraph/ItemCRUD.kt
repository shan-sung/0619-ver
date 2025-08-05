package com.example.myapplication.navigation.subgraph

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.AddScheduleScreen
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SearchMapsWrapper
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SelectFromMapScreen
import com.example.myapplication.viewmodel.SearchViewModel
import com.example.myapplication.viewmodel.AttractionsViewModel
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel

fun NavGraphBuilder.selectFromMapNavGraph(navController: NavController) {
    composable(
        Routes.MyPlans.SELECT_FROM_SAVED_WITH_ID,
        arguments = listOf(navArgument("travelId") { type = NavType.StringType })
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("travelId") ?: ""

        SelectFromMapScreen(
            navController = navController,
            travelId = travelId, // ✅ 傳入
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

                navController.navigate(Routes.MyPlans.addScheduleRoute(travelId))
            }
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

fun NavGraphBuilder.addScheduleNavGraph(navController: NavController) {
    composable(
        route = Routes.MyPlans.ADD_SCHEDULE,
        arguments = listOf(navArgument("travelId") { type = NavType.StringType })
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("travelId")
        val viewModel: TripDetailViewModel = hiltViewModel()
        val currentTrip = viewModel.travel.collectAsState().value

        LaunchedEffect(travelId) {
            if (travelId != null && currentTrip == null) {
                viewModel.fetchTravelById(travelId)
            }
        }

        val attraction = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<Attraction>("selected_attraction")

        if (currentTrip != null) {
            AddScheduleScreen(
                currentTrip = currentTrip,
                navController = navController,
                attraction = attraction
            )
        } else {
            Text("載入行程中...")
        }
    }
}
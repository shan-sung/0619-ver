package com.example.myapplication.navigation.subgraph

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.AddScheduleScreen
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.EditScheduleScreen
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SearchMapsWrapper
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SelectFromMapScreen
import com.example.myapplication.viewmodel.SearchViewModel
import com.example.myapplication.viewmodel.AttractionsViewModel
import com.example.myapplication.viewmodel.TripDetailViewModel

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
        if (travelId.isNullOrBlank()) {
            Text("無效的旅程 ID")
            return@composable
        }

        val viewModel: TripDetailViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val travel = uiState.data
        val isLoading = uiState.isLoading
        val error = uiState.error

        LaunchedEffect(travelId) {
            if (travel == null || travel._id != travelId) {
                viewModel.fetchTravelById(travelId)
            }
        }

        val attraction = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<Attraction>("selected_attraction")

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text("錯誤：$error")
            travel != null && attraction != null -> AddScheduleScreen(
                currentTrip = travel,
                navController = navController,
                attraction = attraction
            )
            travel != null && attraction == null -> Text("未選擇景點")
            else -> Text("未知錯誤，請重試")
        }
    }
}

fun NavGraphBuilder.editScheduleNavGraph(navController: NavController) {
    composable(
        route = Routes.MyPlans.EDIT_SCHEDULE,
        arguments = listOf(
            navArgument("travelId") { type = NavType.StringType },
            navArgument("day") { type = NavType.IntType },
            navArgument("index") { type = NavType.IntType }
        )
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("travelId")
        val day = backStackEntry.arguments?.getInt("day")
        val index = backStackEntry.arguments?.getInt("index")

        if (travelId.isNullOrBlank() || day == null || index == null) {
            Text("無效的編輯參數")
            return@composable
        }

        val viewModel: TripDetailViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val travel = uiState.data
        val isLoading = uiState.isLoading
        val error = uiState.error

        // ⏬ 確保正確資料載入
        LaunchedEffect(travelId) {
            if (travel == null || travel._id != travelId) {
                viewModel.fetchTravelById(travelId)
            }
        }

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text("錯誤：$error")
            travel != null -> {
                val scheduleItem = travel.itinerary
                    ?.find { it.day == day }
                    ?.schedule
                    ?.getOrNull(index)

                if (scheduleItem != null) {
                    EditScheduleScreen(
                        currentTrip = travel,
                        navController = navController,
                        scheduleItem = scheduleItem,
                        itemIndex = index
                    )
                } else {
                    Text("找不到要編輯的行程點")
                }
            }

            else -> {
                Text("未知錯誤")
            }
        }
    }
}

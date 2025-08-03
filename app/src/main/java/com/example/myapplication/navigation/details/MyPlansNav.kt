package com.example.myapplication.navigation.details

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.data.model.Travel
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.b_myplans._comp.CreatedTripsScreen
import com.example.myapplication.ui.screens.b_myplans._comp.ParticipatingTripsScreen
import com.example.myapplication.ui.screens.b_myplans._comp.TripTab
import com.example.myapplication.ui.screens.b_myplans.b_prev.CreateTripWizardScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.PreviewScreen
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.TripScreen
import com.example.myapplication.ui.screens.b_myplans.d_features.ChatRoomScreen
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SearchMapsWrapper
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.SelectFromMapScreen
import com.example.myapplication.viewmodel.ForYouViewModel
import com.example.myapplication.viewmodel.myplans.PreviewViewModel
import com.example.myapplication.viewmodel.myplans.TripCreationViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun TripNavHost(
    navController: NavHostController,
    startDestination: TripTab,
    parentNavController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination.route,
        modifier = modifier
    ) {
        composable(Routes.MyPlans.Tab.CREATED) {
            CreatedTripsScreen(navController = parentNavController)
        }
        composable(Routes.MyPlans.Tab.PARTICIPATING) {
            ParticipatingTripsScreen(navController = parentNavController)
        }
    }
}

fun NavGraphBuilder.tripDetailNav(navController: NavController) {
    composable(
        route = Routes.MyPlans.DETAIL,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStackEntry ->
        val travelId = backStackEntry.arguments?.getString("id") ?: return@composable
        TripScreen(navController = navController, travelId = travelId)
    }
}

fun NavGraphBuilder.createNav(navController: NavController) {
    composable(Routes.MyPlans.CREATE) {
        CreateTripWizardScreen(navController = navController)
    }
}

fun NavGraphBuilder.chatNav() {
    composable(
        route = Routes.MyPlans.CHAT,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("id") ?: return@composable
        ChatRoomScreen(tripId = tripId)
    }
}

fun NavGraphBuilder.selectFromMapNav(navController: NavController) {
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
                // TODO: 可寫入 Draft 行程中
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

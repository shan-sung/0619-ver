package com.example.myapplication.navigation.details

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.myplans.CreatedTripsScreen
import com.example.myapplication.ui.screens.myplans.ParticipatingTripsScreen
import com.example.myapplication.ui.screens.myplans.TripTab
import com.example.myapplication.ui.screens.myplans.creation.CreateTripWizardScreen
import com.example.myapplication.ui.screens.myplans.trip.ChatRoomScreen
import com.example.myapplication.ui.screens.myplans.trip.TripScreen

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
    composable(Routes.MyPlans.CHAT) {
        ChatRoomScreen()
    }
}
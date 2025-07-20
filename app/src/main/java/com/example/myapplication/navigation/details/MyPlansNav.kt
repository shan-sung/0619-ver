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
import com.example.myapplication.model.ItineraryDay
import com.example.myapplication.model.ScheduleItem
import com.example.myapplication.model.ScheduleTime
import com.example.myapplication.model.Travel
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.screens.myplans.CreatedTripsScreen
import com.example.myapplication.ui.screens.myplans.ParticipatingTripsScreen
import com.example.myapplication.ui.screens.myplans.PreviewScreen
import com.example.myapplication.ui.screens.myplans.TripTab
import com.example.myapplication.ui.screens.myplans.creation.CreateTripWizardScreen
import com.example.myapplication.ui.screens.myplans.trip.ChatRoomScreen
import com.example.myapplication.ui.screens.myplans.trip.SelectFromSavedListScreen
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
    composable(
        route = Routes.MyPlans.CHAT,
        arguments = listOf(navArgument("id") { type = NavType.StringType })
    ) { backStackEntry ->
        val tripId = backStackEntry.arguments?.getString("id") ?: return@composable
        ChatRoomScreen(tripId = tripId)
    }
}

fun NavGraphBuilder.selectFromSavedNav(navController: NavController) {
    composable(Routes.MyPlans.SELECT_FROM_SAVED) {
        SelectFromSavedListScreen(
            onSelect = {}, navController = navController
        )
    }
}

fun NavGraphBuilder.previewNav() {
    composable(Routes.MyPlans.PREVIEW) { backStackEntry ->
//        val travel = backStackEntry.savedStateHandle?.get<Travel>("travel")
        val travel = fakeTravel()
        if (travel != null) {
            PreviewScreen(
                travel = travel,
                onConfirm = {
                    // TODO: 送出 confirm API 並跳轉行程列表或詳細頁
                },
                onRegenerate = {
                    // TODO: 重新送出 trip-request 並更新 preview
                }
            )
        }
    }
}

fun fakeTravel(): Travel {
    return Travel(
        _id = "t123",
        userId = "u001",
        chatRoomId = "c001",
        members = listOf("u001", "u002"),
        created = false,
        title = "Paris Trip",
        startDate = "2025-08-01",
        endDate = "2025-08-05",
        budget = 20000,
        description = "A romantic getaway",
        imageUrl = "https://source.unsplash.com/featured/?eiffel-tower",
        itinerary = listOf(
            ItineraryDay(
                day = 1,
                schedule = listOf(
                    ScheduleItem(1, ScheduleTime("09:00", "10:00"), "Eiffel Tower visit", "Walk"),
                    ScheduleItem(1, ScheduleTime("12:00", "13:00"), "Lunch at bistro", "Walk"),
                    ScheduleItem(1, ScheduleTime("14:00", "16:00"), "Louvre Museum exploration", "Metro")
                )
            ),
            ItineraryDay(
                day = 2,
                schedule = listOf(
                    ScheduleItem(2, ScheduleTime("10:00", "12:00"), "Notre Dame", "Metro"),
                    ScheduleItem(2, ScheduleTime("13:00", "15:00"), "Seine River cruise", "Boat")
                )
            ),
            ItineraryDay(
                day = 3,
                schedule = listOf(
                    ScheduleItem(3, ScheduleTime("10:00", "11:00"), "Montmartre", "Metro"),
                    ScheduleItem(3, ScheduleTime("11:30", "13:00"), "Sacré-Cœur Basilica", "Walk")
                )
            ),
            ItineraryDay(
                day = 4,
                schedule = listOf(
                    ScheduleItem(4, ScheduleTime("09:30", "12:00"), "Versailles Palace", "Train"),
                    ScheduleItem(4, ScheduleTime("13:00", "15:00"), "Champs-Élysées", "Metro")
                )
            ),
            ItineraryDay(
                day = 5,
                schedule = listOf(
                    ScheduleItem(5, ScheduleTime("09:00", "10:00"), "Departure", "Taxi")
                )
            )
        )
    )
}

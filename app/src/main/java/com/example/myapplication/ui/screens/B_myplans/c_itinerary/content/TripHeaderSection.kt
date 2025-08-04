package com.example.myapplication.ui.screens.b_myplans.c_itinerary.content

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myapplication.data.model.Travel
import com.example.myapplication.ui.components.card.TripInfoHeader

@Composable
fun TripHeaderSection(
    travel: Travel,
    isInTrip: Boolean,
    navController: NavController
) {
    TripInfoHeader(
        travel = travel,
        isInTrip = isInTrip,
        navController = navController
    )
}

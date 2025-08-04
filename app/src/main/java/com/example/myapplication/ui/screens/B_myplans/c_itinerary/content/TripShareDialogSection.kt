package com.example.myapplication.ui.screens.b_myplans.c_itinerary.content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavController
import com.example.myapplication.data.model.Travel
import com.example.myapplication.ui.screens.b_myplans.d_features.ShareTripDialog

@Composable
fun TripShareDialogSection(
    navController: NavController,
    travel: Travel
) {
    val showShareDialogForTripId = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("show_share_dialog", null)
        ?.collectAsState()?.value

    if (showShareDialogForTripId != null) {
        ShareTripDialog(
            tripId = showShareDialogForTripId,
            memberIds = travel.members,
            onDismiss = {
                navController.currentBackStackEntry?.savedStateHandle?.set("show_share_dialog", null)
            }
        )
    }
}

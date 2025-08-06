package com.example.myapplication.ui.screens.b_myplans.c_itinerary.content

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myapplication.data.model.Travel
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.crud.AddPlaceDialog
import com.example.myapplication.viewmodel.TripDetailViewModel

@Composable
fun TripAddDialogSection(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    travel: Travel,
    navController: NavController,
    viewModel: TripDetailViewModel
) {
    if (showDialog) {
        AddPlaceDialog(
            currentTrip = travel,
            navController = navController,
            viewModel = viewModel,
            onDismiss = onDismiss
        )
    }
}

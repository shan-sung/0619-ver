package com.example.myapplication.ui.screens.b_myplans.c_itinerary.content

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.components.AppFab

@Composable
fun TripFabSection(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppFab(
        onClick = onClick,
        icon = Icons.Filled.Add,
        contentDescription = "Add Itinerary",
        modifier = modifier
    )
}
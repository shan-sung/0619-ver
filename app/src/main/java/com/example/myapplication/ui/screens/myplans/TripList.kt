package com.example.myapplication.ui.screens.myplans

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myapplication.model.Travel
import com.example.myapplication.ui.components.ColCard
import com.example.myapplication.ui.components.toInfoCardData

@Composable
fun TripList(trips: List<Travel>, navController: NavController) {
    LazyColumn {
        items(trips) { trip ->
            ColCard(trip.toInfoCardData(navController))
        }
    }
}
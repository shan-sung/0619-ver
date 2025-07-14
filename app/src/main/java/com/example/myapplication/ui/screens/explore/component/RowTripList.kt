package com.example.myapplication.ui.screens.explore.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.Travel
import com.example.myapplication.ui.components.InfoCard
import com.example.myapplication.ui.components.toInfoCardData

@Composable
fun RowTripList(navController: NavController, travels: List<Travel>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(travels) { travel ->
            InfoCard(
                data = travel.toInfoCardData(navController),
                width = 240.dp,
                height = 160.dp
            )
        }
    }
}
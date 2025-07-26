package com.example.myapplication.ui.screens.explore

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication.model.Attraction
import com.example.myapplication.ui.components.AttractionInfoCardVertical
import com.example.myapplication.ui.components.InfoCardVertical
import com.example.myapplication.ui.components.toInfoCardData

fun LazyListScope.nearbyAttractionsSection(
    attractions: List<Attraction>,
    navController: NavController,
    context: Context,
    onItemClick: (Attraction) -> Unit
) {
    stickyHeader {
        SectionHeader(
            title = "Nearby Attractions",
            actionText = "more",
            onActionClick = { navController.navigate("shared_itineraries") },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        )
    }

    items(attractions) { a ->
        AttractionInfoCardVertical(
            attraction = a,
            context = context,
            onItemClick = onItemClick
        )
    }
}
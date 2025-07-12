package com.example.myapplication.ui.screens.explore

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myapplication.model.Travel
import com.example.myapplication.ui.components.CardRowLib

@Composable
fun FeaturedSection(navController: NavController, travels: List<Travel>) {
    SectionWithHeader(
        title = "Featured",
        onMoreClick = { navController.navigate("featured/more") }
    ) {
        CardRowLib(navController = navController, travels = travels)
    }
}
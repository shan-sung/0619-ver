package com.example.myapplication.ui.screens.explore.section

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myapplication.model.Travel
import com.example.myapplication.ui.screens.explore.component.SectionWithHeader
import com.example.myapplication.ui.screens.explore.component.RowTripList

@Composable
fun FeaturedSection(navController: NavController, travels: List<Travel>) {
    SectionWithHeader(
        title = "Featured",
        onMoreClick = { navController.navigate("featured") }
    ) {
        RowTripList(navController = navController, travels = travels)
    }
}


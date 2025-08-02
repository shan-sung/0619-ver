package com.example.myapplication.ui.screens.a_explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Travel
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.InfoCard

fun LazyListScope.popularTripsSection(travels: List<Travel>, navController: NavController) {
    stickyHeader {
        SectionHeader(
            title = "Hot Itineraries",
            actionText = "more",
            onActionClick = { navController.navigate(Routes.Explore.Featured.MAIN) },
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        )
    }

    item {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(travels) { travel ->
                InfoCard(
                    travel = travel,
                    width = 240.dp,
                    height = 160.dp,
                    onClick = {
                        navController.navigate(Routes.MyPlans.detailRoute(travel._id))
                    },
                    buttonText = "聊天室",
                    onButtonClick = {
                        navController.navigate(Routes.MyPlans.chatRoute(travel._id))
                    }
                )
            }
        }
    }
}

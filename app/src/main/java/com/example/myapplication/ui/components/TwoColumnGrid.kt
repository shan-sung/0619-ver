package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.Travel
import androidx.navigation.NavController
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.card.InfoCard

@Composable
fun TwoColumnCardGrid(
    items: List<Travel>,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(items.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    travel = rowItems[0],
                    aspectRatio = 1f,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        navController.navigate(Routes.MyPlans.detailRoute(rowItems[0]._id))
                    },
                    onButtonClick = {
                        navController.navigate(Routes.MyPlans.chatRoute(rowItems[0]._id))
                    }
                )

                if (rowItems.size > 1) {
                    InfoCard(
                        travel = rowItems[1],
                        aspectRatio = 1f,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(Routes.MyPlans.detailRoute(rowItems[1]._id))
                        },
                        onButtonClick = {
                            navController.navigate(Routes.MyPlans.chatRoute(rowItems[1]._id))
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
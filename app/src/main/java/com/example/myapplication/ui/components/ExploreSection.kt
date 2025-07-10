package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.Attraction
import com.example.myapplication.data.Travel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExploreSection(
    navController: NavController,
    isLoading: Boolean,
    attractions: List<Attraction>,
    onRefresh: () -> Unit,
    travels: List<Travel>
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Section: Featured
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Featured")
            MoreIconButton(
                onClick = { navController.navigate("featured/more") },
                contentDescription = "查看更多行程"
            )
        }
        CardRowLib(navController = navController, travels = travels)

        // Section: Attractions with pull-to-refresh
        val pullRefreshState = rememberPullRefreshState(refreshing = isLoading, onRefresh = onRefresh)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .pullRefresh(pullRefreshState)
        ) {
            Column {
                // Attractions 標題列
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader("Attractions")
                    MoreIconButton(
                        onClick = { navController.navigate("attractions/more") },
                        contentDescription = "查看更多景點"
                    )
                }

                // 🟡 這裡是圈圈的位置：標題和內容之間
                if (isLoading) {
                    PullRefreshIndicator(
                        refreshing = true,
                        state = pullRefreshState,
                        modifier = Modifier
//                            .padding(vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }

                when {
                    isLoading && attractions.isEmpty() -> {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    attractions.isEmpty() -> {
                        Text(
                            "附近沒有景點",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(16.dp)
                                .testTag("no-attractions")
                        )
                    }
                    else -> {
                        AttractionList(attractions = attractions)
                    }
                }
            }
        }
    }
}

package com.example.myapplication.ui.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.Attraction
import com.example.myapplication.ui.components.AttractionList
import com.example.myapplication.viewmodel.SavedViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AttractionsSection(
    navController: NavController,
    isLoading: Boolean,
    attractions: List<Attraction>,
    onRefresh: () -> Unit,
    savedViewModel: SavedViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    // PullRefresh 狀態管理
    val pullRefreshState = rememberPullRefreshState(refreshing = isLoading, onRefresh = onRefresh)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .pullRefresh(pullRefreshState)
    ) {
        Column {
            SectionWithHeader(
                title = "Attractions",
                onMoreClick = { navController.navigate("attractions/more") }
            ) {
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
                        AttractionList(
                            attractions = attractions,
                            savedViewModel = savedViewModel,
                            snackbarHostState = snackbarHostState,
                            coroutineScope = coroutineScope
                        )
                    }
                }
            }
        }
    }
}

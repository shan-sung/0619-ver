package com.example.myapplication.ui.screens.explore.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.Travel
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.SectionWithHeader
import com.example.myapplication.ui.screens.explore.component.AttractionList
import com.example.myapplication.ui.screens.explore.component.RowTripList
import com.example.myapplication.viewmodel.saved.SavedViewModel
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExploreSection(
    navController: NavController,
    isLoading: Boolean,
    attractions: List<Attraction>,
    onRefresh: () -> Unit,
    travels: List<Travel>,
    savedViewModel: SavedViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            FeaturedSection(navController, travels)
            AttractionsSection(
                navController,
                isLoading,
                attractions,
                onRefresh,
                savedViewModel,
                snackbarHostState,
                coroutineScope
            )
        }
    }
}

@Composable
fun FeaturedSection(navController: NavController, travels: List<Travel>) {
    SectionWithHeader(
        title = "Featured",
        onMoreClick = { navController.navigate(Routes.Explore.Featured.MAIN) }
    ) {
        RowTripList(navController = navController, travels = travels)
    }
}


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
                onMoreClick = { navController.navigate(Routes.Explore.Attraction.MAIN) }
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

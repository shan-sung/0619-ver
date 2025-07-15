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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
    nearbyAttractions: List<Attraction>,
    recommendedAttractions: List<Attraction>,
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

            //  猜你喜歡
            RecommendedSection(
                navController = navController,
                recommendedAttractions = recommendedAttractions,
                savedViewModel = savedViewModel,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope
            )

            // 附近熱門景點
            AttractionsSection(
                navController = navController,
                isLoading = isLoading,
                nearbyAttractions = nearbyAttractions,
                onRefresh = onRefresh,
                savedViewModel = savedViewModel,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope
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


// 首頁部分只顯示附近熱門景點
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AttractionsSection(
    navController: NavController,
    isLoading: Boolean,
    nearbyAttractions: List<Attraction>,
    onRefresh: () -> Unit,
    savedViewModel: SavedViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    val pullRefreshState = rememberPullRefreshState(refreshing = isLoading, onRefresh = onRefresh)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .pullRefresh(pullRefreshState)
    ) {
        Column {
            SectionWithHeader(
                title = "附近熱門景點",
                onMoreClick = { navController.navigate(Routes.Explore.Attraction.MAIN) }
            ) {
                when {
                    isLoading && nearbyAttractions.isEmpty() -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    nearbyAttractions.isEmpty() -> {
                        Text(
                            "附近沒有熱門景點",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(16.dp)
                                .testTag("no-attractions")
                        )
                    }

                    else -> {
                        AttractionList(
                            attractions = nearbyAttractions,
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

@Composable
fun RecommendedSection(
    navController: NavController,
    recommendedAttractions: List<Attraction>,
    savedViewModel: SavedViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    if (recommendedAttractions.isNotEmpty()) {
        SectionWithHeader(
            title = "猜你喜歡"
        ) {
            AttractionList(
                attractions = recommendedAttractions,
                savedViewModel = savedViewModel,
                snackbarHostState = snackbarHostState,
                coroutineScope = coroutineScope
            )
        }
    }
}

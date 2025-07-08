package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.ItineraryDay
import com.example.myapplication.data.Travel
import com.example.myapplication.ui.components.InfoCard
import com.example.myapplication.ui.components.ScheduleList
import com.example.myapplication.viewmodel.TripDetailViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TripScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    travelId: String,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val travelFlow = remember(travelId) { viewModel.getTravelById(travelId) }
    val travel by travelFlow.collectAsState(initial = null)

    if (travel != null) {
        TripContent(navController = navController, travel = travel!!)
    } else {
        CircularProgressIndicator()
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TripContent(
    navController: NavController,
    travel: Travel
) {
    val days = travel.days ?: 0
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { days }
    )
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TripInfoCard(navController = navController, travel = travel)

        if (days > 0) {
            TripDayTabs(
                days = days,
                pagerState = pagerState,
                coroutineScope = coroutineScope
            )

            TripPager(
                pagerState = pagerState,
                days = days,
                itinerary = travel.itinerary ?: emptyList(), // 👈 修正這裡
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TripInfoCard(navController: NavController, travel: Travel) {
    InfoCard(
        navController = navController,
        travelId = travel._id,
        title = travel.title,
        subtitle = listOfNotNull(
            travel.members?.let { "$it member${if (it > 1) "s" else ""}" },
            travel.days?.let { "$it day${if (it > 1) "s" else ""}" },
            travel.budget?.let { "Budget: ${String.format(Locale.US, "$%,d", it)}" }
        ).joinToString(" · ")
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TripDayTabs(
    days: Int,
    pagerState: PagerState,
    coroutineScope: CoroutineScope
) {
    val tabTitles = (1..days).map { "Day $it" }

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 16.dp
    ) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                text = { Text(title) },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TripPager(
    pagerState: PagerState,
    days: Int,
    itinerary: List<ItineraryDay>,
    modifier: Modifier = Modifier
) {
    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val item = itinerary.find { it.day == page + 1 }
        DayContent(dayIndex = page, itineraryDay = item)
    }
}


@Composable
fun DayContent(dayIndex: Int, itineraryDay: ItineraryDay?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Day ${dayIndex + 1} - ${itineraryDay?.date ?: "無日期"}",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (itineraryDay != null) {
            ScheduleList(schedule = itineraryDay.schedule)
        } else {
            Text("尚無行程資料")
        }
    }
}
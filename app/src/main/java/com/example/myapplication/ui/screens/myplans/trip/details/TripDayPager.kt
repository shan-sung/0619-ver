package com.example.myapplication.ui.screens.myplans.trip.details

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.ItineraryDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

@Composable
fun TripPager(
    pagerState: PagerState,
    days: Int,
    itinerary: List<ItineraryDay>,
    startDate: String,
    modifier: Modifier = Modifier
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val tripStart = LocalDate.parse(startDate, formatter)

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val item = itinerary.find { it.day == page + 1 }
        val computedDate = tripStart.plusDays(page.toLong()).format(formatter)

        DayContent(
            dayIndex = page,
            itineraryDay = item,
            dateOverride = computedDate
        )
    }
}
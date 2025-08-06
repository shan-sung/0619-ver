package com.example.myapplication.ui.screens.b_myplans.c_itinerary.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.myapplication.data.model.ItineraryDay
import com.example.myapplication.data.model.Travel
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.elemtent.TripDayTabs
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.elemtent.TripPager
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDate


@Composable
fun TripPagerSection(
    days: Int,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    itinerary: List<ItineraryDay>,
    startDate: LocalDate,
    endDate: LocalDate,
    travel: Travel,                          // ✅ 加這行
    navController: NavController,            // ✅ 加這行
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TripDayTabs(
            days = days,
            pagerState = pagerState,
            coroutineScope = coroutineScope,
            startDate = startDate,
            endDate = endDate
        )

        TripPager(
            pagerState = pagerState,
            days = travel.days,
            itinerary = travel.itinerary.orEmpty(),
            startDate = travel.startDate,
            travelId = travel._id,
            navController = navController
        )
    }
}

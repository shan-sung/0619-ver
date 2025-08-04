package com.example.myapplication.ui.screens.b_myplans.c_itinerary.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.data.model.ItineraryDay
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
    modifier: Modifier = Modifier  // ✅ 新增
) {
    Column(modifier = modifier) {  // ✅ 包起來才能使用 weight
        TripDayTabs(
            days = days,
            pagerState = pagerState,
            coroutineScope = coroutineScope,
            startDate = startDate,
            endDate = endDate
        )

        TripPager(
            pagerState = pagerState,
            days = days,
            itinerary = itinerary,
            startDate = startDate.toString(),
            modifier = Modifier.fillMaxSize()
        )
    }
}

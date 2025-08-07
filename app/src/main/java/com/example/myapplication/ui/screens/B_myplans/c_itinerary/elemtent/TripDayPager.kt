package com.example.myapplication.ui.screens.b_myplans.c_itinerary.elemtent

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.ItineraryDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import androidx.navigation.NavController


@Composable
fun TripDayTabs(
    days: Int,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    startDate: LocalDate,
    endDate: LocalDate
) {
    val context = LocalContext.current
    val tabTitles = (1..days).map { "Day $it" }

    Box(modifier = Modifier.fillMaxWidth()) {
        // TabRow + Today 按鈕
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Scrollable Tab Row
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 16.dp,
                modifier = Modifier
                    .weight(1f),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                    )
                },
                divider = {
                    Divider(
                        thickness = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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

            // 垂直分隔線
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .width(1.dp)
                    .height(24.dp)
                    .background(color = MaterialTheme.colorScheme.outlineVariant)
            )

            // Today 按鈕
            TextButton(
                onClick = {
                    val todayIndex = getTodayIndex(startDate, endDate)
                    if (todayIndex != null) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(todayIndex)
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "今天不在行程範圍內喔",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Today")
            }
        }
    }
}


@Composable
fun TripPager(
    pagerState: PagerState,
    days: Int,
    itinerary: List<ItineraryDay>,
    startDate: String,
    travelId: String,                      // ✅ 新增
    navController: NavController,         // ✅ 新增
    modifier: Modifier = Modifier
) {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("MM/dd（E）", Locale.TAIWAN)
    val tripStart = LocalDate.parse(startDate, inputFormatter)

    HorizontalPager(
        state = pagerState,
        modifier = modifier
    ) { page ->
        val day = itinerary.find { it.day == page + 1 }
        val formattedDate = tripStart.plusDays(page.toLong()).format(outputFormatter)

        DayContent(
            dayIndex = page + 1,
            itineraryDay = day,
            pagerState = pagerState,
            dateOverride = formattedDate,
            travelId = travelId,             // ✅ 傳入
            navController = navController    // ✅ 傳入
        )
    }
}


fun getTodayIndex(startDate: LocalDate, endDate: LocalDate): Int? {
    val today = LocalDate.now()
    return if (!today.isBefore(startDate) && !today.isAfter(endDate)) {
        ChronoUnit.DAYS.between(startDate, today).toInt()
    } else null
}

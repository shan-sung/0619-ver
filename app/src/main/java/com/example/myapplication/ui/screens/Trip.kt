package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.ItineraryDay
import com.example.myapplication.data.Travel
import com.example.myapplication.ui.components.AddFab
import com.example.myapplication.ui.components.AddScheduleDialog
import com.example.myapplication.ui.components.InfoCard
import com.example.myapplication.ui.components.Timeline
import com.example.myapplication.viewmodel.TripDetailViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TripScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    travelId: String,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val travelState = remember { mutableStateOf<Travel?>(null) }

    LaunchedEffect(travelId) {
        viewModel.getTravelById(travelId).collect { travel ->
            travelState.value = travel
        }
    }

    val travel = travelState.value

    if (travel != null) {
        TripContent(
            navController = navController,
            travelState = travelState // ✅ 傳狀態本體進去
        )
    } else {
        CircularProgressIndicator()
    }

}

@Composable
fun TripContent(
    navController: NavController,
    travelState: MutableState<Travel?>
) {
    val travel = travelState.value ?: return
    val days = travel.days ?: 0
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { days })
    val coroutineScope = rememberCoroutineScope()
    val showDialog = remember { mutableStateOf(false) }
    val tripStartDate = travel.startDate?.let { LocalDate.parse(it) } ?: LocalDate.now()
    val tripEndDate = travel.endDate?.let { LocalDate.parse(it) } ?: LocalDate.now().plusDays((days - 1).toLong())

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp), // 不保留任何 FAB 空間
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TripInfoCard(navController = navController, travel = travel)

            if (days > 0) {
                TripDayTabs(days = days, pagerState = pagerState, coroutineScope = coroutineScope)

                TripPager(
                    pagerState = pagerState,
                    days = days,
                    itinerary = travel.itinerary ?: emptyList(),
                    startDate = travel.startDate, // ✅ 加這行
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 完全浮動的 FAB，不影響 layout
        AddFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = { showDialog.value = true }
        )

        if (showDialog.value) {
            AddScheduleDialog(
                travelId = travel._id,
                tripStartDate = tripStartDate,
                tripEndDate = tripEndDate,
                onDismiss = { showDialog.value = false },
                onScheduleAdded = { newItem ->
                    val currentTravel = travelState.value ?: return@AddScheduleDialog
                    val updatedItinerary = currentTravel.itinerary?.toMutableList() ?: mutableListOf()

                    val dayEntry = updatedItinerary.find { it.day == newItem.day }

                    if (dayEntry != null) {
                        val updatedSchedule = dayEntry.schedule.toMutableList()
                        updatedSchedule.add(newItem)
                        updatedItinerary[updatedItinerary.indexOf(dayEntry)] =
                            dayEntry.copy(schedule = updatedSchedule)
                    } else {
                        // 新增該天的 ItineraryDay 並自動補上正確日期
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val startDate = LocalDate.parse(travel.startDate, formatter)
                        val date = startDate.plusDays((newItem.day - 1).toLong()).format(formatter)

                        updatedItinerary.add(
                            ItineraryDay(
                                day = newItem.day,
                                schedule = listOf(newItem)
                            )
                        )
                    }
                    travelState.value = currentTravel.copy(itinerary = updatedItinerary)
                }
            )
        }

    }
}


@Composable
fun TripInfoCard(navController: NavController, travel: Travel) {
    InfoCard(
        navController = navController,
        travelId = travel._id,
        title = travel.title ?: "未命名行程",
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
            dateOverride = computedDate // ✅ 加這個
        )
    }
}

@Composable
fun DayContent(dayIndex: Int, itineraryDay: ItineraryDay?, dateOverride: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Day ${dayIndex + 1} - $dateOverride", // ✅ 強制使用 dateOverride
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (itineraryDay != null) {
            Timeline(schedule = itineraryDay.schedule)
        } else {
            Text("尚無行程資料")
        }
    }
}

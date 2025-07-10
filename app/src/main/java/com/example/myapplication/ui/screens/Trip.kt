package com.example.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.ItineraryDay
import com.example.myapplication.data.Travel
import com.example.myapplication.ui.components.AddFab
import com.example.myapplication.ui.components.AddScheduleDialog
import com.example.myapplication.ui.components.InfoCard
import com.example.myapplication.ui.components.ScheduleList
import com.example.myapplication.ui.components.SheetItem
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
            travelState = travelState
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
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val tripStartDate = travel.startDate?.let { LocalDate.parse(it) } ?: LocalDate.now()
    val tripEndDate = travel.endDate?.let { LocalDate.parse(it) } ?: LocalDate.now().plusDays((days - 1).toLong())

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Box(
                    Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.LightGray, RoundedCornerShape(2.dp))
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text("Add New Itinerary", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                SheetItem("From Search") { /* handle click */ }
                SheetItem("From Saved List") { /* handle click */ }
                SheetItem("Hand-Input") { showDialog.value = true }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TripInfoCard(navController = navController, travel = travel)

                if (days > 0) {
                    TripDayTabs(days = days, pagerState = pagerState, coroutineScope = coroutineScope)

                    TripPager(
                        pagerState = pagerState,
                        days = days,
                        itinerary = travel.itinerary ?: emptyList(),
                        startDate = travel.startDate ?: "",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            AddFab(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onClick = {
                    coroutineScope.launch {
                        sheetState.show()
                    }
                }
            )

            if (showDialog.value) {
                AddScheduleDialog(
                    travelId = travel._id,
                    tripStartDate = tripStartDate,
                    tripEndDate = tripEndDate,
                    onDismiss = { showDialog.value = false },
                    onScheduleAdded = { /* update state */ }
                )
            }
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
            dateOverride = computedDate
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
            text = "Day ${dayIndex + 1} - $dateOverride",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (itineraryDay != null) {
            ScheduleList(schedule = itineraryDay.schedule, modifier = Modifier.weight(1f))
        } else {
            Text("尚無行程資料")
        }
    }
}
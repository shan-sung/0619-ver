package com.example.myapplication.ui.screens.b_myplans.c_itinerary.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.Travel
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.viewmodel.TripDetailViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripContent(
    navController: NavController,
    travel: Travel,
    selectedAttraction: Attraction?,
    currentUserId: String = CurrentUser.user?.id.orEmpty(),
    scrollToDay: Int?,
    onScheduleAdded: (ScheduleItem) -> Unit,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val days = travel.days
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { days })
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val tripStartDate = LocalDate.parse(travel.startDate)
    val tripEndDate = LocalDate.parse(travel.endDate)

    val isOwner = travel.userId == currentUserId
    val isIn = travel.members.contains(currentUserId) || isOwner

    var showDialog by remember { mutableStateOf(false) }
    var defaultLocation by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(scrollToDay) {
        scrollToDay?.let { pagerState.scrollToPage(it - 1) }
    }

    LaunchedEffect(selectedAttraction) {
        selectedAttraction?.let {
            defaultLocation = it.name
            showDialog = true
            navController.currentBackStackEntry?.savedStateHandle?.set("selected_attraction", null)
        }
    }

    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) sheetState.show() else sheetState.hide()
    }

    TripShareDialogSection(navController, travel)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TripHeaderSection(travel, isIn, navController)

            if (days > 0) {
                TripPagerSection(
                    days = days,
                    pagerState = pagerState,
                    coroutineScope = coroutineScope,
                    itinerary = travel.itinerary ?: emptyList(),
                    startDate = tripStartDate,
                    endDate = tripEndDate,
                    travel = travel,                      // ✅ 傳入 travel
                    navController = navController         // ✅ 傳入 navController
                )
            }
        }

        if (isOwner) {
            TripFabSection(
                onClick = { showBottomSheet = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )

        }

        TripAddDialogSection(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            travel = travel,
            navController = navController,
            viewModel = viewModel
        )

        TripBottomSheetSection(
            showBottomSheet = showBottomSheet,
            sheetState = sheetState,
            onDismiss = { showBottomSheet = false },
            onFromMap = {
                showBottomSheet = false
                navController.navigate(Routes.MyPlans.selectFromSavedRoute(travel._id))
            },
            onHandInput = {
                showBottomSheet = false
                showDialog = true
            }
        )
    }
}

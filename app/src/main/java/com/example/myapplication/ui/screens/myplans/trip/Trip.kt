
package com.example.myapplication.ui.screens.myplans.trip

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.CurrentUser
import com.example.myapplication.model.ItineraryDay
import com.example.myapplication.model.ScheduleItem
import com.example.myapplication.model.Travel
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.AppFab
import com.example.myapplication.ui.components.InfoCard
import com.example.myapplication.ui.components.RoundedIconButton
import com.example.myapplication.ui.components.ScheduleTimeline
import com.example.myapplication.ui.components.SheetItem
import com.example.myapplication.ui.components.dialogs.AddScheduleDialog
import com.example.myapplication.ui.components.toInfoCardData
import com.example.myapplication.viewmodel.friend.FriendViewModel
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TripScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    travelId: String,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val travel by viewModel.travel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val savedStateHandle = currentBackStackEntry?.savedStateHandle

    // 觀察從 Saved 傳回來的 Attraction
    val selectedAttraction = savedStateHandle?.getStateFlow<Attraction?>("selected_attraction", null)
        ?.collectAsState()?.value


    LaunchedEffect(travelId) {
        viewModel.fetchTravelById(travelId)
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else if (travel != null) {
        TripContent(
            navController = navController,
            travel = travel!!,
            selectedAttraction = selectedAttraction,  // ✅ 傳進去
            onScheduleAdded = {
                Log.d("TripScreen", "已新增新行程項目：${it.placeName}")
            }
        )
    } else {
        Text("找不到行程")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripContent(
    navController: NavController,
    travel: Travel,
    selectedAttraction: Attraction?, // ✅ 新增參數
    currentUserId: String = CurrentUser.user?.id.orEmpty(),
    onScheduleAdded: (ScheduleItem) -> Unit
) {
    val days = travel.days
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { days })
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val tripStartDate = LocalDate.parse(travel.startDate)
    val tripEndDate = LocalDate.parse(travel.endDate)
    val isOwner = travel.userId == currentUserId
    val isIn = travel.members.contains(currentUserId) || travel.userId == currentUserId
    var showDialog by remember { mutableStateOf(false) }
    var defaultLocation by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(selectedAttraction) {
        selectedAttraction?.let {
            defaultLocation = it.name
            showDialog = true
            navController.currentBackStackEntry?.savedStateHandle?.set("selected_attraction", null)
        }
    }

    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    val showShareDialogForTripId = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("show_share_dialog", null)
        ?.collectAsState()?.value

    if (showShareDialogForTripId != null) {
        ShareTripDialog(
            tripId = showShareDialogForTripId,
            memberIds = travel.members,  // 傳入已在行程中的 userId 清單
            onDismiss = {
                navController.currentBackStackEntry?.savedStateHandle?.set("show_share_dialog", null)
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TripInfoCard(navController = navController, travel = travel, showButton = isIn)

            if (days > 0) {
                TripDayTabs(days = days, pagerState = pagerState, coroutineScope = coroutineScope)

                TripPager(
                    pagerState = pagerState,
                    days = days,
                    itinerary = travel.itinerary ?: emptyList(),
                    startDate = travel.startDate,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (isOwner) {
            AppFab(
                onClick = {
                    showBottomSheet = true
                },
                icon = Icons.Filled.Add,
                contentDescription = "Add Itinerary",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }

        if (showDialog) {
            AddScheduleDialog(
                travelId = travel._id,
                tripStartDate = tripStartDate,
                tripEndDate = tripEndDate,
                onDismiss = {
                    showDialog = false
                },
                onScheduleAdded = { item ->
                    onScheduleAdded(item)
                    showDialog = false
                },
                initialLocation = defaultLocation
            )
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Add New Itinerary", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    SheetItem("From Search") { /* handle click */ }
                    SheetItem("From Saved List") {
                        showBottomSheet = false
                        navController.navigate(Routes.MyPlans.SELECT_FROM_SAVED)
                    }
                    SheetItem("Hand-Input") {
                        showBottomSheet = false
                        showDialog = true
                    }
                }
            }
        }
    }
}

@Composable
fun TripInfoCard(
    navController: NavController,
    travel: Travel,
    showButton: Boolean
) {
    val cardData = travel.toInfoCardData(navController, showButton = showButton)

    Box {
        InfoCard(
            data = cardData.copy(
                buttonText = null,  // 不顯示原本的文字按鈕
                onButtonClick = null
            ),
            width = 360.dp,
            height = 200.dp
        )

        if (showButton) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (travel.userId == CurrentUser.user?.id || travel.members.contains(CurrentUser.user?.id)) {
                    RoundedIconButton(
                        icon = Icons.Default.MonetizationOn,
                        description = "預算",
                        onClick = {
                            navController.navigate(Routes.MyPlans.chatRoute(travel._id.orEmpty()))
                        }
                    )
                    RoundedIconButton(
                        icon = Icons.Default.Chat,
                        description = "聊天室",
                        onClick = {
                            navController.navigate(Routes.MyPlans.chatRoute(travel._id.orEmpty()))
                        }
                    )
                    RoundedIconButton(
                        icon = Icons.Default.PersonAdd,
                        description = "分享行程",
                        onClick = {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("show_share_dialog", travel._id)
                        }
                    )
                }
            }
        }
    }
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
            ScheduleTimeline(schedule = itineraryDay.schedule, modifier = Modifier.weight(1f))
        } else {
            Text("尚無行程資料")
        }
    }
}

@Composable
fun ShareTripDialog(
    tripId: String,
    memberIds: List<String>,
    onDismiss: () -> Unit
) {
    val viewModel: TripDetailViewModel = hiltViewModel()
    val context = LocalContext.current
    var triggerShare by remember { mutableStateOf(false) }
    var showFriendPicker by remember { mutableStateOf(false) }


    // 🔁 真正處理分享行為
    LaunchedEffect(triggerShare) {
        if (triggerShare) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, "加入我的旅程：點此開啟 ➜ https://yourapp.com/trips/$tripId")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "分享行程"))
            triggerShare = false // 重置狀態
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("分享行程") },
        text = {
            Column {
                Text("選擇一種方式分享行程")
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        showFriendPicker = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("從好友列表中選擇")
                }
                if (showFriendPicker) {
                    FriendPickerDialog(
                        onDismiss = { showFriendPicker = false },
                        onConfirm = { selectedFriendIds ->
                            viewModel.inviteFriends(tripId, selectedFriendIds) { success ->
                                showFriendPicker = false
                                onDismiss()
                            }
                        },
                        existingMemberIds = memberIds  // ✅ 傳入成員 ID
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        triggerShare = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("分享連結")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun FriendPickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    existingMemberIds: List<String>,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val friends by viewModel.friendList.collectAsState()
    val selectedIds = remember {
        mutableStateListOf<String>().apply {
            addAll(existingMemberIds) // 預設勾選已有成員
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("邀請朋友加入行程") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                friends.forEach { friend ->
                    val isAlreadyInTrip = existingMemberIds.contains(friend.id)
                    val isSelected = selectedIds.contains(friend.id)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (!isAlreadyInTrip) Modifier.clickable {
                                    if (isSelected) selectedIds.remove(friend.id)
                                    else selectedIds.add(friend.id)
                                } else Modifier // 不可點擊
                            )
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null,
                            enabled = !isAlreadyInTrip  // ✅ 禁用已在 trip 的 checkbox
                        )
                        Text(
                            text = friend.username + if (isAlreadyInTrip) "（已加入）" else "",
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (isAlreadyInTrip) Color.Gray else Color.Unspecified
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // ✅ 僅傳回非既有成員
                val newInvites = selectedIds.filterNot { existingMemberIds.contains(it) }
                onConfirm(newInvites)
            }) {
                Text("確定加入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
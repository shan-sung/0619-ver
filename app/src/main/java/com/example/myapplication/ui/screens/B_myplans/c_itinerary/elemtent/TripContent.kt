package com.example.myapplication.ui.screens.b_myplans.c_itinerary.elemtent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.CurrentUser
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.Travel
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.AppFab
import com.example.myapplication.ui.components.SheetItem
import com.example.myapplication.ui.components.dialogs.AddScheduleDialog
import com.example.myapplication.ui.screens.b_myplans.d_features.ShareTripDialog
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripContent(
    navController: NavController,
    travel: Travel,                          // 當前行程資料
    selectedAttraction: Attraction?,         // 從 Saved 地圖頁面傳回的景點（可用來預設地點）
    currentUserId: String = CurrentUser.user?.id.orEmpty(),  // 目前使用者 ID（預設取自全域登入資訊）
    onScheduleAdded: (ScheduleItem) -> Unit  // 當成功新增行程項目時的 callback
) {
    // 行程天數（用來決定有幾頁 DayTab + Pager）
    val days = travel.days

    // 建立可記憶的 Pager 狀態（起始頁為 Day 1）
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { days })

    // 協程作用域，用於點擊 Tab 時控制 Pager 捲動動畫
    val coroutineScope = rememberCoroutineScope()

    // 控制底部 Modal Bottom Sheet 的狀態
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 行程開始/結束日期（用於日期選擇或範圍檢查）
    val tripStartDate = LocalDate.parse(travel.startDate)
    val tripEndDate = LocalDate.parse(travel.endDate)

    // 是否為該行程建立者（只有擁有者可以新增項目）
    val isOwner = travel.userId == currentUserId

    // 判斷是否為行程成員（含主揪也算）
    val isIn = travel.members.contains(currentUserId) || travel.userId == currentUserId

    // 控制是否顯示手動新增行程項目的 Dialog
    var showDialog by remember { mutableStateOf(false) }

    // 若從地圖頁面選取地點返回時，預設地點名稱會放進這個變數中
    var defaultLocation by remember { mutableStateOf("") }

    // 控制是否顯示 ModalBottomSheet（來源選擇）
    var showBottomSheet by remember { mutableStateOf(false) }

    // ⚡ 監聽：若從 Saved 地圖頁返回並選取了地點，則預設地點並開啟 Dialog
    LaunchedEffect(selectedAttraction) {
        selectedAttraction?.let {
            defaultLocation = it.name
            showDialog = true

            // 重置回 null，避免 Dialog 一直開啟
            navController.currentBackStackEntry?.savedStateHandle?.set("selected_attraction", null)
        }
    }

    // 當 showBottomSheet 狀態變化時，打開或關閉 Sheet
    LaunchedEffect(showBottomSheet) {
        if (showBottomSheet) sheetState.show() else sheetState.hide()
    }

    // 取得是否應該開啟分享 Dialog（由 InfoCard 上按鈕觸發）
    val showShareDialogForTripId = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("show_share_dialog", null)
        ?.collectAsState()?.value

    // 若有指定 tripId，要開啟分享 Dialog（包含好友邀請、複製連結等）
    if (showShareDialogForTripId != null) {
        ShareTripDialog(
            tripId = showShareDialogForTripId,
            memberIds = travel.members,
            onDismiss = {
                navController.currentBackStackEntry?.savedStateHandle?.set("show_share_dialog", null)
            }
        )
    }

    // 畫面主結構（上方資訊卡 + DayTabs + Pager）
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顯示行程卡片與右上角功能按鈕（預算、聊天室、分享）
            TripInfoCard(navController = navController, travel = travel, showButton = isIn)

            if (days > 0) {
                // 顯示 Day1、Day2… 的可點擊 Tab
                TripDayTabs(days = days, pagerState = pagerState, coroutineScope = coroutineScope)

                // 對應顯示每日行程頁面
                TripPager(
                    pagerState = pagerState,
                    days = days,
                    itinerary = travel.itinerary ?: emptyList(),
                    startDate = travel.startDate,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 若使用者為主揪，顯示右下角「新增」FAB
        if (isOwner) {
            AppFab(
                onClick = { showBottomSheet = true },
                icon = Icons.Filled.Add,
                contentDescription = "Add Itinerary",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }

        // 顯示手動新增行程的 Dialog（使用者可填寫地點、時間等）
        if (showDialog) {
            AddScheduleDialog(
                travelId = travel._id,
                tripStartDate = tripStartDate,
                tripEndDate = tripEndDate,
                onDismiss = { showDialog = false },
                onScheduleAdded = {
                    onScheduleAdded(it)
                    showDialog = false
                },
                initialLocation = defaultLocation // 從 Maps 回傳的預設地點
            )
        }

        // 顯示底部選擇來源的 Modal Bottom Sheet（Maps or Hand-Input）
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
                    Text("Add New Itinerary", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Maps（從 Google Maps 搜尋與儲存過的景點）
                    SheetItem("From Maps") {
                        showBottomSheet = false
                        navController.navigate(Routes.MyPlans.SELECT_FROM_SAVED)
                    }

                    // 手動輸入（打開 AddScheduleDialog）
                    SheetItem("Hand-Input") {
                        showBottomSheet = false
                        showDialog = true
                    }
                }
            }
        }
    }
}
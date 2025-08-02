package com.example.myapplication.ui.screens.b_myplans.c_itinerary

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.elemtent.TripContent
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel

@Composable
fun TripScreen(
    navController: NavController,
    travelId: String,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    // 觀察 Travel 資料（行程物件），由 ViewModel 提供
    val travel by viewModel.travel.collectAsState()

    // 觀察是否正在載入資料，用於顯示 Loading Spinner
    val isLoading by viewModel.isLoading.collectAsState()

    // 取得目前頁面的 SavedStateHandle，用來存取 navigation 傳遞的資料（例如選擇的景點）
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val savedStateHandle = currentBackStackEntry?.savedStateHandle

    // 從 SavedStateHandle 中觀察 "selected_attraction"（從 Saved 頁面傳回的 Attraction）
    val selectedAttraction = savedStateHandle
        ?.getStateFlow<Attraction?>("selected_attraction", null)
        ?.collectAsState()?.value

    // ⚡ 當 Trip 畫面一被載入時，用 tripId 呼叫 ViewModel 取得對應行程資料
    LaunchedEffect(travelId) {
        viewModel.fetchTravelById(travelId)
    }

    // UI 呈現邏輯：依照 loading 狀態與 travel 資料是否為 null，決定顯示內容
    when {
        // 顯示載入中動畫
        isLoading -> {
            CircularProgressIndicator()
        }

        // 成功取得行程資料，渲染 TripContent 畫面
        travel != null -> {
            TripContent(
                navController = navController,
                travel = travel!!,  // 已非 null，可安全使用 !!
                selectedAttraction = selectedAttraction, // 傳入是否從地圖選擇景點
                onScheduleAdded = {
                    // TODO: 可在此處加上 snackbar 提示、log 記錄等行為
                }
            )
        }

        // 如果找不到對應行程（travel == null 且非 loading 狀態）
        else -> {
            Text("找不到行程")
        }
    }
}
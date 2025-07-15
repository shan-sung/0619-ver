package com.example.myapplication.ui.screens.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.screens.explore.section.ExploreSection
import com.example.myapplication.util.getCurrentOrFallbackLocation
import com.example.myapplication.viewmodel.explore.AttractionsViewModel
import com.example.myapplication.viewmodel.explore.TripsViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel
import kotlinx.coroutines.launch

@Composable
fun ExploreScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    savedViewModel: SavedViewModel = hiltViewModel() // ✅ 在這統一建立
) {
    /* ViewModel */
    val viewModel: AttractionsViewModel = hiltViewModel() // AttractionViewModel
    val tripsViewModel: TripsViewModel = hiltViewModel() // TripsViewModel

    /*  State Listening */
    val attractions by viewModel.attractions.collectAsState() // 景點清單
    val allTrips by tripsViewModel.trips.collectAsState() // 行程清單
    val isLoading by viewModel.isLoading.collectAsState() // 景點載入中

    val coroutineScope = rememberCoroutineScope()

    /* 位置偵測與資料載入邏輯 */
    val context = LocalContext.current
    // 使用者開啟畫面時，就自動取得位置並載入附近景點
    LaunchedEffect(Unit) {
        val latLng = getCurrentOrFallbackLocation(context)
        viewModel.fetchNearbyAttractions(latLng) // 第一次手動抓一次
        tripsViewModel.fetchAllTrips()  // 抓行程
    }

    // UI
    Column(modifier = modifier.fillMaxWidth()) {
        ExploreSection(
            navController = navController,
            isLoading = isLoading,
            attractions = attractions,
            onRefresh = {
                coroutineScope.launch {
                    val latLng = getCurrentOrFallbackLocation(context)
                    viewModel.fetchNearbyAttractions(latLng) // 使用者手動刷新
                }
            },
            travels = allTrips,
            savedViewModel = savedViewModel // 傳遞共用 ViewModel
        )
    }
}

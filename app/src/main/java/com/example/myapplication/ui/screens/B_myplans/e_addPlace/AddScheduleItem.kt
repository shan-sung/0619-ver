package com.example.myapplication.ui.screens.b_myplans.e_addPlace

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.components.bar.OverlayScreenWithCloseIcon

@Composable
fun AddScheduleScreen(
    navController: NavController,
    attraction: Attraction?
) {
    OverlayScreenWithCloseIcon(
        onClose = { navController.popBackStack() },
        title = "新增行程點"
    ) {
        if (attraction != null) {
            Text("地點：${attraction.name}")
            Text("地址：${attraction.address ?: "無資料"}")
            // 這裡可以放時間選擇器、交通方式、備註等欄位
        } else {
            Text("未提供景點資訊")
        }
    }
}

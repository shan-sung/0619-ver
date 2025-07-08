package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.InfoCard
import com.example.myapplication.viewmodel.TripDetailViewModel
import java.util.Locale

@Composable
fun TripScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    travelId: String,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
// 使用 remember 避免重複觸發資料流
    val travelFlow = remember(travelId) {
        viewModel.getTravelById(travelId)
    }
    val travel by travelFlow.collectAsState(initial = null)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (travel != null) {
                InfoCard(
                    navController = navController,
                    travelId = travel!!._id,
                    title = travel!!.title,
                    subtitle = listOfNotNull(
                        travel!!.members?.let { "$it member${if (it > 1) "s" else ""}" },
                        travel!!.days?.let { "$it day${if (it > 1) "s" else ""}" },
                        travel!!.budget?.let {
                            "Budget: ${String.format(Locale.US, "$%,d", it)}"
                        }
                    ).joinToString(" · ")
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}

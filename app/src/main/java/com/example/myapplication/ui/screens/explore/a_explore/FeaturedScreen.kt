package com.example.myapplication.ui.screens.a_explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.SectionWithHeader
import com.example.myapplication.ui.components.TwoColumnCardGrid
import com.example.myapplication.viewmodel.TripsViewModel

@Composable
fun FeaturedScreen(
    navController: NavController,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val trips = uiState.data.orEmpty()

    LaunchedEffect(Unit) {
        viewModel.fetchAllTrips()
    }

    Column {
        SectionWithHeader(title = "Featured") {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
                uiState.error != null -> {
                    Text(
                        text = "發生錯誤：${uiState.error}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                trips.isEmpty() -> {
                    Text("目前沒有推薦行程", modifier = Modifier.padding(16.dp))
                }
                else -> {
                    TwoColumnCardGrid(items = trips, navController = navController)
                }
            }
        }
    }
}

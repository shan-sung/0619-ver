package com.example.myapplication.ui.screens.explore

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
import com.example.myapplication.ui.components.toInfoCardData
import com.example.myapplication.viewmodel.explore.TripsViewModel

@Composable
fun FeaturedScreen(
    navController: NavController,
    viewModel: TripsViewModel = hiltViewModel()
) {
    val trips by viewModel.trips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchAllTrips()
    }

    Column {
        SectionWithHeader(title = "Featured") {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                error != null -> Text("發生錯誤：$error", color = Color.Red, modifier = Modifier.padding(16.dp))
                trips.isEmpty() -> Text("目前沒有推薦行程", modifier = Modifier.padding(16.dp))
                else -> TwoColumnCardGrid(
                    items = trips.map { it.toInfoCardData(navController) }
                )
            }
        }
    }
}
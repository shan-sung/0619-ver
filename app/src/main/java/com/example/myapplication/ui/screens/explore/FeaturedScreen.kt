package com.example.myapplication.ui.screens.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.myapplication.model.Travel
import com.example.myapplication.ui.components.RowCard
import com.example.myapplication.ui.components.toInfoCardData
import com.example.myapplication.ui.screens.explore.component.SectionWithHeader
import com.example.myapplication.viewmodel.TripsViewModel

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
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                }
                error != null -> {
                    Text("發生錯誤：$error", color = Color.Red, modifier = Modifier.padding(16.dp))
                }
                trips.isEmpty() -> {
                    Text("目前沒有推薦行程", modifier = Modifier.padding(16.dp))
                }
                else -> {
                    FeaturedList(navController = navController, travels = trips)
                }
            }
        }
    }
}

@Composable
fun FeaturedList(navController: NavController, travels: List<Travel>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(travels.chunked(2)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RowCard(
                    navController = navController,
                    data = rowItems[0].toInfoCardData(navController),
                    aspectRatio = 1f,
                    modifier = Modifier.weight(1f)
                )
                if (rowItems.size > 1) {
                    RowCard(
                        navController = navController,
                        data = rowItems[1].toInfoCardData(navController),
                        aspectRatio = 1f,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f)) // 補空白避免不對稱
                }
            }
        }
    }
}

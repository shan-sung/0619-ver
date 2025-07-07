package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.RowInfoCard
import com.example.myapplication.viewmodel.TripDetailViewModel
import java.util.Locale

@Composable
fun TripScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    travelId: String,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val travel by viewModel.getTravelById(travelId).collectAsState(initial = null)

    Scaffold(modifier = modifier) { innerPadding ->
        travel?.let {
            Column(modifier = Modifier.padding(innerPadding)) {
                RowInfoCard(
                    navController = navController,
                    travelId = it._id,
                    title = it.title,
                    subtitle = listOfNotNull(
                        it.members?.let { "$it member${if (it > 1) "s" else ""}" },
                        it.days?.let { "$it day${if (it > 1) "s" else ""}" },
                        it.budget?.let {
                            "Budget: ${String.format(Locale.US, "$%,d", it)}"
                        }
                    ).joinToString(" · ")
                )
            }
        } ?: run {
            // Loading 狀態
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

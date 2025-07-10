package com.example.myapplication.ui.screens.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.Attraction
import com.example.myapplication.data.Travel
import com.example.myapplication.viewmodel.SavedViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExploreSection(
    navController: NavController,
    isLoading: Boolean,
    attractions: List<Attraction>,
    onRefresh: () -> Unit,
    travels: List<Travel>,
    savedViewModel: SavedViewModel // ✅ 加入這行
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            FeaturedSection(navController, travels)
            AttractionsSection(
                navController,
                isLoading,
                attractions,
                onRefresh,
                savedViewModel,
                snackbarHostState,
                coroutineScope
            )
        }
    }
}
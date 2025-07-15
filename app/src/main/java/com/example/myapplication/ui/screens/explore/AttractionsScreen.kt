package com.example.myapplication.ui.screens.explore

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.BuildConfig
import com.example.myapplication.model.Attraction
import com.example.myapplication.ui.components.SectionWithHeader
import com.example.myapplication.ui.components.TwoColumnCardGrid
import com.example.myapplication.ui.components.toInfoCardData
import com.example.myapplication.viewmodel.explore.RecommendationViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel
import kotlinx.coroutines.launch

@Composable
fun AttractionsScreen(
    navController: NavController,
    apiKey: String = BuildConfig.MAPS_API_KEY,
    viewModel: RecommendationViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val recommended by viewModel.recommended.collectAsState()

    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchRecommendedAttractions(apiKey)
    }

    Column {
        SectionWithHeader(title = "你可能會喜歡") {
            when {
                recommended.isEmpty() -> Text("暫無推薦景點", modifier = Modifier.padding(16.dp))
                else -> {
                    TwoColumnCardGrid(
                        items = recommended.map {
                            it.toInfoCardData(context).copy(
                                onClick = { selectedAttraction = it }
                            )
                        }
                    )
                }
            }
        }
    }

    selectedAttraction?.let { attraction ->
        AlertDialog(
            onDismissRequest = { selectedAttraction = null },
            title = { Text("你想要做什麼？") },
            text = { Text(attraction.name) },
            confirmButton = {
                TextButton(
                    onClick = {
                        savedViewModel.addToSaved(attraction)
                        selectedAttraction = null
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("已加入收藏：${attraction.name}")
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        val query = Uri.encode(attraction.name)
                        val gmmIntentUri = "geo:0,0?q=$query".toUri()
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                            setPackage("com.google.android.apps.maps")
                        }
                        context.startActivity(
                            if (mapIntent.resolveActivity(context.packageManager) != null)
                                mapIntent
                            else Intent(Intent.ACTION_VIEW, "https://www.google.com/maps/search/?api=1&query=$query".toUri())
                        )
                        selectedAttraction = null
                    }
                ) {
                    Text("Google Maps")
                }
            }
        )
    }
}


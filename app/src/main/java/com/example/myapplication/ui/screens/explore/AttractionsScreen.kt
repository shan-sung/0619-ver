package com.example.myapplication.ui.screens.explore

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.model.Attraction
import com.example.myapplication.ui.components.SectionWithHeader
import com.example.myapplication.ui.components.TwoColumnCardGrid
import com.example.myapplication.ui.components.toInfoCardData
import com.example.myapplication.viewmodel.AttractionsViewModel
import com.example.myapplication.viewmodel.SavedViewModel
import kotlinx.coroutines.launch

@Composable
fun AttractionsScreen(
    navController: NavController,
    location: String = "25.033964,121.564468", // 預設台北101
    viewModel: AttractionsViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val attractions by viewModel.attractions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }

    LaunchedEffect(location) {
        viewModel.fetchNearbyAttractions(location)
    }

    Column {
        SectionWithHeader(title = "附近景點") {
            when {
                isLoading -> CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                error != null -> Text("發生錯誤：$error", color = Color.Red, modifier = Modifier.padding(16.dp))
                attractions.isEmpty() -> Text("附近沒有景點資料", modifier = Modifier.padding(16.dp))
                else -> {
                    TwoColumnCardGrid(
                        items = attractions.map {
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

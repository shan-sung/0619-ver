package com.example.myapplication.ui.screens.explore

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.model.Attraction
import com.example.myapplication.viewmodel.explore.AttractionsViewModel
import com.example.myapplication.viewmodel.explore.RecommendationViewModel
import com.example.myapplication.viewmodel.explore.TripsViewModel
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun ExploreScreen(
    navController: NavController,
    tripsViewModel: TripsViewModel = hiltViewModel(),
    attractionsViewModel: AttractionsViewModel = hiltViewModel(),
    savedViewModel: SavedViewModel = hiltViewModel(),
    recommendationViewModel: RecommendationViewModel = hiltViewModel()
) {
    val travels by tripsViewModel.trips.collectAsState()
    val attractions by attractionsViewModel.attractions.collectAsState()
    val savedAttractions by savedViewModel.savedAttractions.collectAsState()
    val recommendations by recommendationViewModel.recommendations.collectAsState()

    val context = LocalContext.current
    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }

    // 初始化資料
    LaunchedEffect(Unit) {
        tripsViewModel.fetchAllTrips()
        attractionsViewModel.fetchNearbyAttractions(context)
        savedViewModel.fetchSavedAttractions()
    }

    LaunchedEffect(savedAttractions, attractions) {
        if (savedAttractions.isNotEmpty() && attractions.isNotEmpty()) {
            recommendationViewModel.updateRecommendations(savedAttractions, attractions)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            if (travels.isNotEmpty()) {
                popularTripsSection(travels, navController)
            }

            if (recommendations.isNotEmpty()) {
                recommendedAttractionsSection(
                    attractions = recommendations,
                    onShuffle = {
                        recommendationViewModel.shuffleRecommendations(savedAttractions, attractions)
                    },
                    context = context,
                    onItemClick = { selectedAttraction = it }
                )
            }

            if (attractions.isNotEmpty()) {
                nearbyAttractionsSection(
                    attractions = attractions,
                    navController = navController,
                    context = context,
                    onItemClick = { selectedAttraction = it }
                )
            }
        }

        selectedAttraction?.let { attraction ->
            Log.d("DIALOG_STATE", "顯示 Dialog：${attraction.name}")
            AttractionActionDialog(
                attraction = attraction,
                dialogTitle = attraction.name,
                onDismiss = { selectedAttraction = null },
                onSave = {
                    val attractionWithTags = attraction.copy(
                        tags = attraction.tags ?: listOf("unknown")
                    )
                    savedViewModel.addToSaved(attractionWithTags)
                    Toast.makeText(context, "已收藏：${attraction.name}", Toast.LENGTH_SHORT).show()
                },
                onOpenMap = {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(attraction.name)}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                        setPackage("com.google.android.apps.maps")
                    }
                    context.startActivity(mapIntent)
                }
            )
        }
    }
}


@Composable
fun AttractionActionDialog(
    attraction: Attraction,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onOpenMap: () -> Unit,
    dialogTitle: String
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(dialogTitle, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                attraction.imageUrl?.let { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text("Rating: ${attraction.rating}/5")
                Spacer(modifier = Modifier.height(4.dp))
                Text(attraction.description ?: "No description.")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onOpenMap) {
                        Text("Google Maps")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onSave) {
                        Text("收藏")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDismiss) {
                        Text("關閉")
                    }
                }
            }
        }
    }
}

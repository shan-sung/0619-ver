package com.example.myapplication.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.data.Attraction
import com.example.myapplication.data.Travel
import com.example.myapplication.viewmodel.SavedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Locale

data class InfoCardData(
    val id: String? = null,
    val title: String,
    val subtitle: String = "",
    val location: String = "",
    val imageUrl: String? = null,
    val mapSearchQuery: String? = null,
    val onClick: (() -> Unit)? = null
)

fun Travel.toInfoCardData(navController: NavController): InfoCardData {
    val subtitleParts = listOfNotNull(
        members?.let { "$it 人" },
        "$days 天", // ✅ 不用 ?.let
        budget?.let { "預算 ${String.format(Locale.US, "%,d", it)} 元" }
    )


    return InfoCardData(
        id = _id,
        title = title ?: "未命名行程",
        subtitle = subtitleParts.joinToString("・"),
        location = "$startDate 至 $endDate",
        imageUrl = imageUrl,
        onClick = {
            navController.navigate("trip_detail/${_id}")
        }
    )
}


fun Attraction.toInfoCardData(context: Context): InfoCardData {
    return InfoCardData(
        title = name,
        subtitle = "${rating ?: 0.0} 星",
        location = city, // ✅ address 欄位並不存在，就用 city 即可
        imageUrl = imageUrl,
        mapSearchQuery = name,
        onClick = {
            val query = Uri.encode(name)
            val gmmIntentUri = "geo:0,0?q=$query".toUri()
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                val webUri = "https://www.google.com/maps/search/?api=1&query=$query".toUri()
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                context.startActivity(webIntent)
            }
        }
    )
}

@Composable
fun TripList(trips: List<Travel>, navController: NavController) {
    LazyColumn {
        items(trips) { trip ->
            InfoCard(trip.toInfoCardData(navController))
        }
    }
}

@Composable
fun AttractionList(
    attractions: List<Attraction>,
    savedViewModel: SavedViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
)
 {
    val context = LocalContext.current
    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }

    LazyColumn {
        items(attractions) { a ->
            InfoCard(
                data = a.toInfoCardData(context).copy(
                    onClick = { selectedAttraction = a }
                )
            )
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

                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val webUri = "https://www.google.com/maps/search/?api=1&query=$query".toUri()
                            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                            context.startActivity(webIntent)
                        }

                        selectedAttraction = null
                    }
                ) {
                    Text("Google Maps")
                }
            }
        )
    }
}
@Composable
fun InfoCard(
    data: InfoCardData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { data.onClick?.invoke() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = data.imageUrl ?: "https://via.placeholder.com/80",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = data.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun RowInfoCard(
    navController: NavController,
    data: InfoCardData,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = {
            Log.d("TravelNavigate", "Navigating to trip_detail/${data.id}")
            try {
                navController.navigate("trip_detail/${data.id}")
            } catch (e: Exception) {
                Log.e("TravelNavigate", "Navigation error", e)
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .width(280.dp)
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                        startY = 300f
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
                )
            }
        }
    }
}

@Composable
fun CardRowLib(navController: NavController, travels: List<Travel>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(travels) { travel ->
            RowInfoCard(
                navController = navController,
                data = travel.toInfoCardData(navController)
            )
        }
    }
}
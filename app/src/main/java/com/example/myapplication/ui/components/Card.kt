package com.example.myapplication.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.Attraction
import com.example.myapplication.data.Travel
import java.util.Locale

sealed class InfoCardData {
    abstract val location: String
    abstract val title: String
    abstract val subtitle: String
    abstract val imageUrl: String?
    open val id: String? = null
    open val mapSearchQuery: String? = null
}

data class TravelInfoCardData(
    override val id: String,
    override val location: String,
    override val title: String,
    override val subtitle: String,
    override val imageUrl: String?
) : InfoCardData()

data class AttractionInfoCardData(
    override val location: String,
    override val title: String,
    override val subtitle: String,
    override val imageUrl: String?,
    override val mapSearchQuery: String
) : InfoCardData()

fun formatTravelSubtitle(travel: Travel): String {
    return listOfNotNull(
        travel.members?.let { "$it 人" },
        travel.days?.let { "$it 天" },
        travel.budget?.let { "預算 ${String.format(Locale.US,"%,d", it)} 元" }
    ).joinToString("・")
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
            val data = TravelInfoCardData(
                id = travel._id,
                location = "旅遊行程",
                title = travel.title,
                subtitle = formatTravelSubtitle(travel),
                imageUrl = travel.imageUrl
            )

            RowInfoCard(
                navController = navController,
                data = data
            )
        }
    }
}

@Composable
fun ColInfoCard(
    modifier: Modifier = Modifier,
    navController: NavController,
    data: InfoCardData,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Card(
        onClick = {
            when (data) {
                is AttractionInfoCardData -> {
                    val query = Uri.encode(data.mapSearchQuery)
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

                is TravelInfoCardData -> {
                    navController.navigate("trip_detail/${data.id}")
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.location,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (data.imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(data.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 100.dp, height = 72.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(width = 100.dp, height = 72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                )
            }
        }
    }
}


@Composable
fun CardColLib(navController: NavController, attractions: List<Attraction>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(attractions) { attraction ->
            val attractionCardData = AttractionInfoCardData(
                location = attraction.city,
                title = attraction.name,
                subtitle = "${attraction.rating ?: 0.0} 星",
                imageUrl = attraction.imageUrl,
                mapSearchQuery = attraction.name
            )

            ColInfoCard(
                navController = navController,
                data = attractionCardData
            )
        }
    }
}

@Composable
fun CardColLibForTravels(navController: NavController, travels: List<Travel>) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(travels) { travel ->
            val travelCardData = TravelInfoCardData(
                id = travel._id,
                location = "旅遊行程",
                title = travel.title,
                subtitle = formatTravelSubtitle(travel),
                imageUrl = travel.imageUrl
            )

            ColInfoCard(
                navController = navController,
                data = travelCardData,
                onClick = {
                    navController.navigate("trip_detail/${travel._id}")
                }
            )
        }
    }
}

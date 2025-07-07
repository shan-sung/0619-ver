package com.example.myapplication.ui.components

import android.content.Intent
import android.net.Uri
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

data class InfoCardData(
    val location: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String? = null,
    val mapSearchQuery: String? = null
)

@Composable
fun RowInfoCard(
    navController: NavController,
    travelId: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { navController.navigate("travel/$travelId") },
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
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
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
                travelId = travel._id,
                title = travel.title,
                subtitle = listOfNotNull(
                    travel.members?.let { "$it member${if (it > 1) "s" else ""}" },
                    travel.days?.let { "$it day${if (it > 1) "s" else ""}" },
                    travel.budget?.let { "Budget: ${String.format(Locale.US, "$%,d", travel.budget)}"
                    }
                ).joinToString(" · ")
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
            onClick?.invoke() ?: run {
                val query = Uri.encode(data.mapSearchQuery ?: data.title)
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
            val infoCardData = InfoCardData(
                location = attraction.city,
                title = attraction.name,
                subtitle = "${attraction.rating ?: 0.0} 星",
                imageUrl = attraction.imageUrl,
                mapSearchQuery = attraction.name
            )
            ColInfoCard(
                navController = navController,
                data = infoCardData
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
            val infoCardData = InfoCardData(
                location = "旅遊行程",
                title = travel.title,
                subtitle = "${travel.days}天・${travel.members}人・預算 ${travel.budget} 元",
                imageUrl = travel.imageUrl
            )

            ColInfoCard(
                navController = navController,
                data = infoCardData,
                onClick = {
                    navController.navigate("travel_detail/${travel._id}")
                }
            )
        }
    }
}

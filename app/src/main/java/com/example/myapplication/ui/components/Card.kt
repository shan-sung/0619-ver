package com.example.myapplication.ui.components

import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.data.Attraction
import com.example.myapplication.data.Travel
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
fun AttractionList(attractions: List<Attraction>) {
    val context = LocalContext.current
    LazyColumn {
        items(attractions) { a ->
            InfoCard(a.toInfoCardData(context))
        }
    }
}

@Composable
fun InfoCard(
    data: InfoCardData,
    modifier: Modifier = Modifier
) {
    val cardModifier = modifier
        .fillMaxWidth()
        .height(100.dp)

    Card(
        onClick = { data.onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        modifier = cardModifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            Modifier.fillMaxSize().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(data.location, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(data.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), maxLines = 1)
                Text(data.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (data.imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(data.imageUrl),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp, 72.dp).clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(modifier = Modifier.size(100.dp, 72.dp).background(Color.Gray).clip(RoundedCornerShape(12.dp)))
            }
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
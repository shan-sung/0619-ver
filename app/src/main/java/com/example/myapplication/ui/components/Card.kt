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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.Travel
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
        "${members.size} people",
        "$days days",
        budget?.let { "Budget $${String.format(Locale.US, "%,d", it)}" }
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
        location = city,
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
fun ColCard( // Used for TripList and AttractionList
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
fun RowCard(
    navController: NavController,
    data: InfoCardData,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1.75f // 預設 280:160 = 1.75
) {
    val imageUrl = data.imageUrl ?: "https://via.placeholder.com/280x160"

    Card(
        onClick = {
            try {
                navController.navigate("trip_detail/${data.id}")
            } catch (e: Exception) {
                Log.e("TravelNavigate", "Navigation error", e)
            }
        },
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .width(280.dp)
            .aspectRatio(aspectRatio) // 依據寬度自動算出高度
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f) // 可視比例調整遮罩高度
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

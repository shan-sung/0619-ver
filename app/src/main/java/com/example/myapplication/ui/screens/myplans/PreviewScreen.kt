@file:Suppress("DEPRECATION")

package com.example.myapplication.ui.screens.myplans

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.R
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.ItineraryDay
import com.example.myapplication.model.ScheduleItem
import com.example.myapplication.model.Travel
import com.example.myapplication.ui.components.AppExtendedFab
import com.example.myapplication.ui.components.AttractionInfoCardVertical

fun ScheduleItem.toAttraction(): Attraction {
    return Attraction(
        id = placeId ?: placeName,
        name = placeName ?: placeName,
        city = "",
        country = "",
        description = note,
        imageUrl = "https://your-image-api.com/search?placeId=$placeId" // 或直接傳進來
    )
}


@Composable
fun PreviewScreen(
    travel: Travel,
    onConfirm: () -> Unit,
    onRegenerate: () -> Unit
) {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // 圖片
            val context = LocalContext.current
            val imageUrl = travel.imageUrl?.takeIf { it.isNotBlank() }
            val imageRequest = ImageRequest.Builder(context)
                .data(imageUrl ?: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e")
                .crossfade(true)
                .build()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = imageRequest,
                    contentDescription = "Trip Image",
                    modifier = Modifier
                        .matchParentSize(),
                    contentScale = ContentScale.Crop
                )

                if (imageUrl == null) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "無照片",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 基本資訊
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(label = "Days", value = "${travel.days}")
                DetailItem(label = "Travelers", value = "${travel.members.size}")
                DetailItem(label = "Budgets", value = travel.budget?.toString() ?: "N/A")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 每日行程
            travel.itinerary?.forEach { day ->
                DayScheduleCard(day)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Regenerate 按鈕
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { onRegenerate() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Regenerate")
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Confirm 按鈕
        AppExtendedFab(
            onClick = { onConfirm() },
            contentDescription = "Confirm Trip",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp, start = 12.dp, end = 12.dp),
            text = "Confirm"
        )
    }
}


@Composable
fun DetailItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background) // 淺灰背景，可換成你要的色
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .widthIn(min = 80.dp), // 防止太小
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun DayScheduleCard(day: ItineraryDay) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Day ${day.day}",
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.height(8.dp))

        day.schedule.forEach { schedule ->
            PlaceItemCard(schedule)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun PlaceItemCard(item: ScheduleItem) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    AttractionInfoCardVertical(
        attraction = item.toAttraction(),
        context = context,
        onItemClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth() // ✅ 加這個
    )


    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = item.placeName ?: item.placeName) },
            text = {
                Column {
                    item.placeId?.let { placeId ->
                        val gmapUrl = "https://www.google.com/maps/place/?q=place_id=$placeId"
                        ClickableText(
                            text = AnnotatedString("🔗 查看 Google 地圖"),
                            onClick = { openMapUrl(gmapUrl, context) }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("關閉")
                }
            }
        )
    }
}

fun openMapUrl(url: String, context: Context) {
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
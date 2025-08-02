package com.example.myapplication.ui.screens.b_myplans.b_prev

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.ItineraryDay
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.Travel
import com.example.myapplication.ui.components.AppExtendedFab
import com.example.myapplication.ui.screens.b_myplans.e_addPlace.element.PlaceItem

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
            PlaceItem(
                attraction = schedule.toAttraction(),
                onClick = { /* 你想點擊後做什麼？留空也可以 */ },
                onRemove = {} // 如果不需要刪除功能，就給空實作
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
package com.example.myapplication.ui.screens.myplans

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.myapplication.R
import com.example.myapplication.model.ItineraryDay
import com.example.myapplication.model.Travel
import com.example.myapplication.navigation.details.fakeTravel
import com.example.myapplication.ui.components.AppExtendedFab
import com.example.myapplication.ui.components.AppFab

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
            // 1. 圖片區塊
            val imageRequest = ImageRequest.Builder(LocalContext.current)
                .data(
                    travel.imageUrl
                        ?: "https://images.unsplash.com/photo-1507525428034-b723cf961d3e"
                )
                .crossfade(true)
                .build()

            AsyncImage(
                model = imageRequest,
                contentDescription = "Trip Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.dummy),
                error = painterResource(R.drawable.dummy)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 三欄基本資訊
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailItem(label = "Days", value = "${travel.days}")
                DetailItem(label = "Travelers", value = "${travel.members.size}")
                DetailItem(label = "Budgets", value = travel.budget?.toString() ?: "N/A")
            }

            Spacer(modifier = Modifier.height(24.dp))

            travel.itinerary?.forEach { day ->
                DayScheduleCard(day)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 5. 操作按鈕
            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { onRegenerate() },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("Regenerate")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppExtendedFab(
                    modifier = Modifier.weight(1f),
                    text = "Invite Contacts",
                    onClick = { /* ... */ },
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                )

                AppExtendedFab(
                    modifier = Modifier.weight(1f),
                    text = "Share Link",
                    onClick = { /* ... */ },
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                )
            }

        }
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
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Day ${day.day}",
            style = MaterialTheme.typography.titleLarge
        )

        Column(modifier = Modifier.padding(start = 8.dp)) {
            day.schedule.forEach {
                Text(
                    text = "${it.time.start}  ${it.activity}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreenPreview() {
    PreviewScreen(
        travel = fakeTravel(),
        onConfirm = {},
        onRegenerate = {}
    )
}
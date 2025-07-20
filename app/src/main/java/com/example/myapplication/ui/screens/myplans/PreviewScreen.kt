package com.example.myapplication.ui.screens.myplans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.model.ItineraryDay
import com.example.myapplication.model.Travel

@Composable
fun PreviewScreen(
    travel: Travel,
    onConfirm: () -> Unit,
    onRegenerate: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // 1. 圖片區塊
        AsyncImage(
            model = travel.imageUrl ?: "https://source.unsplash.com/featured/?travel",
            contentDescription = "Trip Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Trip Details 標題
        Text("Trip Details", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // 3. 三欄基本資訊
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DetailItem(label = "Duration", value = "${travel.days} Days")
            DetailItem(label = "Travelers", value = "${travel.members.size}")
            DetailItem(label = "Budgets", value = travel.budget?.toString() ?: "N/A")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Itinerary Summary
        Text("Itinerary Summary", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        travel.itinerary?.forEach { day ->
            DayScheduleCard(day)
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. 操作按鈕
        Text("Invite Friends", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { /* Invite: 之後再實作 */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Invite Contacts", color = Color.Black)
            }

            Button(
                onClick = { /* Share: 之後再實作 */ },
                modifier = Modifier.weight(1f)
            ) {
                Text("Share Link")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Regenerate",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable { onRegenerate() }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Confirm Trip")
        }
    }
}


@Composable
fun DetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun DayScheduleCard(day: ItineraryDay) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Day ${day.day}: ${day.schedule.joinToString("、") { it.activity }}",
            style = MaterialTheme.typography.titleSmall
        )

        Column(modifier = Modifier.padding(start = 8.dp)) {
            day.schedule.forEach {
                Text(
                    text = "${it.time.start}: ${it.activity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

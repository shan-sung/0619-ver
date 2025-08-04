package com.example.myapplication.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.SourceType
import com.example.myapplication.ui.components.placedetaildialog.PlaceDetailDialog
import com.example.myapplication.ui.components.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleTimeline(
    schedule: List<ScheduleItem>,
    modifier: Modifier = Modifier
) {
    val viewModel: TripDetailViewModel = hiltViewModel()
    val travel = viewModel.travel.collectAsState().value

    var selectedItem by remember { mutableStateOf<ScheduleItem?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        val sortedSchedule = schedule.sortedBy { it.startTime }

        itemsIndexed(sortedSchedule) { _, item ->
            ScheduleItemCard(
                item = item,
                onClick = { selectedItem = item }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    selectedItem?.let { item ->
        if (item.place.source == SourceType.GOOGLE && item.place.id != null) {
            val attraction = Attraction(
                id = item.place.id ?: "",
                name = item.place.name,
                address = item.place.address,
                lat = item.place.lat,
                lng = item.place.lng,
                imageUrl = item.place.imageUrl,
                rating = item.place.rating,
                userRatingsTotal = item.place.userRatingsTotal,
                openingHours = item.place.openingHours
            )
            Log.d("DEBUG", "Attraction rating = ${attraction.rating}, total = ${attraction.userRatingsTotal}")

            PlaceDetailDialog(
                attraction = attraction,
                mode = PlaceActionMode.ADD_TO_ITINERARY,
                onDismiss = { selectedItem = null },
                onAddToItinerary = { /* handle */ }
            )
        }
    }
}

@Composable
fun ScheduleItemCard(
    item: ScheduleItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "Event Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.place.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = item.formatTimeRange(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

fun ScheduleItem.formatTimeRange(): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")

    val start = this.startTime
    val end = this.endTime

    return when {
        start != null && end != null -> "${start.format(formatter)} – ${end.format(formatter)}"
        start != null -> "${start.format(formatter)} – 未定"
        end != null -> "未定 – ${end.format(formatter)}"
        else -> "時間未定"
    }
}

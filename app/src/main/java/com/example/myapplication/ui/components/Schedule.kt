package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleTimeline(
    schedule: List<ScheduleItem>,
    modifier: Modifier = Modifier
) {
    val viewModel: TripDetailViewModel = hiltViewModel() // ⬅️ 正確方式取得 ViewModel
    val travel = viewModel.travel.collectAsState().value

    val formatter = DateTimeFormatter.ISO_DATE // 假設是 "yyyy-MM-dd" 格式
    val tripStartDate = travel?.startDate?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()
    val tripEndDate = travel?.endDate?.let { LocalDate.parse(it, formatter) } ?: LocalDate.now()

    val travelId = travel?._id ?: ""
    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }
    var selectedItem by remember { mutableStateOf<ScheduleItem?>(null) }
    var scheduleState by remember { mutableStateOf(schedule) }
    var selectedIndex by remember { mutableStateOf<Int?>(null) }


    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        val sortedSchedule = scheduleState.sortedBy { it.startTime }

        itemsIndexed(sortedSchedule) { index, item ->
            ScheduleItemCard(
                item = item,
                onClick = {
                    selectedItem = item
                    selectedIndex = index  // 🔁 紀錄 index
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

    }

    selectedItem?.let { item ->
        if (item.place.source == SourceType.GOOGLE && item.place.id != null) {
            val attraction = Attraction(
                id = item.place.id,
                name = item.place.name,
                address = item.place.address,
                lat = item.place.lat,
                lng = item.place.lng,
                imageUrl = item.place.imageUrl
            )
            PlaceDetailDialog(
                attraction = attraction,
                mode = PlaceActionMode.ADD_TO_ITINERARY,
                onDismiss = {
                    selectedItem = null
                    selectedIndex = null
                },
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
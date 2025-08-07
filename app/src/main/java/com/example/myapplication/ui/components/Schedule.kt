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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.ScheduleItem
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTimeline(
    schedule: List<ScheduleItem>,
    travelId: String,
    navController: NavController,
    pagerState: PagerState, // 加這個
    modifier: Modifier = Modifier
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val sortedSchedule = remember(schedule) { schedule.sortedBy { it.startTime } } // ✅ 提到前面
    var selectedItemIndex by remember { mutableStateOf<Int?>(null) }
    val selectedItem = selectedItemIndex?.let { sortedSchedule.getOrNull(it) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        itemsIndexed(sortedSchedule) { index, item ->
            ScheduleItemCard(
                item = item,
                onClick = {
                    selectedItemIndex = index
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    selectedItem?.let { item ->
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                    selectedItemIndex = null
                }
            },
            sheetState = bottomSheetState
        ) {
            ScheduleDetailBottomSheet(
                item = item,
                navController = navController,
                travelId = travelId,
                index = selectedItemIndex!!,
                pagerState = pagerState, // ✅ 明確傳入
                onClose = {
                    coroutineScope.launch { bottomSheetState.hide() }
                        .invokeOnCompletion { selectedItemIndex = null }
                }
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

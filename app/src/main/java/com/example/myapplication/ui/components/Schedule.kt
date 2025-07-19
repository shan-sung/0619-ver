package com.example.myapplication.ui.components

import android.app.TimePickerDialog
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.model.ScheduleItem
import com.example.myapplication.model.ScheduleTime
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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

    var selectedItem by remember { mutableStateOf<ScheduleItem?>(null) }
    var scheduleState by remember { mutableStateOf(schedule) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        val sortedSchedule = scheduleState.sortedBy { it.startTime }

        items(sortedSchedule) { item ->
            ScheduleItemCard(
                item = item,
                onClick = { selectedItem = item }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    selectedItem?.let { item ->
        EditScheduleDialog(
            originalItem = item,
            tripStartDate = tripStartDate,
            tripEndDate = tripEndDate,
            travelId = travelId,
            onDismiss = { selectedItem = null },
            onScheduleEdited = { updatedItem ->
                scheduleState = scheduleState.map {
                    if (it == item) updatedItem else it
                }
                selectedItem = null
            }
        )

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
        // 圓形底色 icon         .background(MaterialTheme.colorScheme.surface), // 使用主題的 surface 顏色
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

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.activity,
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

@Composable
fun EditScheduleDialog(
    originalItem: ScheduleItem,
    tripStartDate: LocalDate,
    tripEndDate: LocalDate,
    travelId: String,
    onDismiss: () -> Unit,
    onScheduleEdited: (ScheduleItem) -> Unit,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    var selectedDate by remember {
        mutableStateOf(tripStartDate.plusDays((originalItem.day - 1).toLong()))
    }
    var location by remember { mutableStateOf(originalItem.activity) }
    var transportation by remember { mutableStateOf(originalItem.transportation) }
    var note by remember { mutableStateOf(originalItem.note ?: "") }
    var startTime by remember { mutableStateOf(originalItem.startTime) }
    var endTime by remember { mutableStateOf(originalItem.endTime) }

    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun showTimePicker(initialTime: LocalTime?, onTimeSelected: (LocalTime) -> Unit) {
        val now = initialTime ?: LocalTime.of(9, 0)
        TimePickerDialog(
            context,
            { _, hour, minute -> onTimeSelected(LocalTime.of(hour, minute)) },
            now.hour,
            now.minute,
            true
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("編輯行程") },
        text = {
            Column {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("地點") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                DateSelectorFieldWithOverlay(
                    label = "選擇日期",
                    date = selectedDate,
                    formatter = dateFormatter,
                    onClick = {
                        showDatePicker(context, tripStartDate, tripEndDate) { selected ->
                            selectedDate = selected
                        }
                    }
                )

                TimeSelectorFieldWithOverlay(
                    label = "開始時間",
                    time = startTime,
                    formatter = timeFormatter,
                    onClick = {
                        showTimePicker(startTime) { selected -> startTime = selected }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TimeSelectorFieldWithOverlay(
                    label = "結束時間",
                    time = endTime,
                    formatter = timeFormatter,
                    onClick = {
                        showTimePicker(endTime) { selected -> endTime = selected }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = transportation,
                    onValueChange = { transportation = it },
                    label = { Text("交通方式") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("備註") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            val isValid = location.isNotBlank()
                    && selectedDate != null && startTime != null && endTime != null

            if (isValid) {
                AppExtendedFab(
                    onClick = {
                        val dayIndex = ChronoUnit.DAYS.between(tripStartDate, selectedDate).toInt() + 1

                        val updatedItem = originalItem.copy(
                            day = dayIndex,
                            activity = location,
                            transportation = transportation,
                            note = note,
                            time = ScheduleTime(
                                start = startTime!!.format(timeFormatter),
                                end = endTime!!.format(timeFormatter)
                            )
                        )

                        viewModel.updateScheduleItemAndRefresh(travelId, updatedItem) { success ->
                            if (success) {
                                Toast.makeText(context, "編輯成功", Toast.LENGTH_SHORT).show()
                                onScheduleEdited(updatedItem)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "編輯失敗", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    text = "儲存"
                )
            }
        },
        dismissButton = {
            AppExtendedFab(onClick = onDismiss, text = "取消")
        }
    )
}
package com.example.myapplication.ui.components.dialogs

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.model.ScheduleItem
import com.example.myapplication.model.ScheduleTime
import com.example.myapplication.ui.components.AppExtendedFab
import com.example.myapplication.ui.components.fields.DateSelectorFieldWithOverlay
import com.example.myapplication.ui.components.fields.TimeSelectorFieldWithOverlay
import com.example.myapplication.ui.components.picker.showDatePicker
import com.example.myapplication.ui.components.picker.showTimePickerDialog
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Composable
fun EditScheduleDialog(
    originalItem: ScheduleItem,
    tripStartDate: LocalDate,
    tripEndDate: LocalDate,
    travelId: String,
    scheduleIndex: Int,
    onDismiss: () -> Unit,
    onScheduleEdited: (ScheduleItem) -> Unit,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    // 初始化欄位狀態
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
                        showDatePicker(context, tripStartDate, tripEndDate) {
                            selectedDate = it
                        }
                    }
                )

                TimeSelectorFieldWithOverlay(
                    label = "開始時間",
                    time = startTime,
                    formatter = timeFormatter,
                    onClick = {
                        showTimePickerDialog(context, startTime) { startTime = it }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TimeSelectorFieldWithOverlay(
                    label = "結束時間",
                    time = endTime,
                    formatter = timeFormatter,
                    onClick = {
                        showTimePickerDialog(context, endTime) { endTime = it }
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
            val isValid = location.isNotBlank() && selectedDate != null && startTime != null && endTime != null

            AppExtendedFab(
                onClick = {
                    if (!isValid) {
                        Toast.makeText(context, "請填寫完整欄位", Toast.LENGTH_SHORT).show()
                        return@AppExtendedFab
                    }

                    val newDayIndex = ChronoUnit.DAYS.between(tripStartDate, selectedDate).toInt() + 1

                    val updatedItem = originalItem.copy(
                        day = newDayIndex,
                        activity = location,
                        transportation = transportation,
                        note = note,
                        time = ScheduleTime(
                            start = startTime!!.format(timeFormatter),
                            end = endTime!!.format(timeFormatter)
                        )
                    )

                    viewModel.updateScheduleItemAndRefresh(
                        travelId = travelId,
                        day = newDayIndex,
                        index = scheduleIndex,
                        updatedItem = updatedItem
                    ) { success ->
                        Toast.makeText(context, if (success) "編輯成功" else "編輯失敗", Toast.LENGTH_SHORT).show()
                        if (success) {
                            onScheduleEdited(updatedItem)
                            onDismiss()
                        }
                    }
                },
                text = "儲存",
//                enabled = isValid
            )
        },
        dismissButton = {
            AppExtendedFab(onClick = onDismiss, text = "取消")
        }
    )
}

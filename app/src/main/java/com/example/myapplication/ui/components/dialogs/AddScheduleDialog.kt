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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun AddScheduleDialog(
    travelId: String,
    tripStartDate: LocalDate,
    tripEndDate: LocalDate,
    onDismiss: () -> Unit,
    onScheduleAdded: (ScheduleItem) -> Unit,
    initialLocation: String = "", // ✅ 新增這行
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var location by remember { mutableStateOf(initialLocation) }
    var transportation by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增行程") },
        text = {
            Column {
                OutlinedTextField(
                    value = location, onValueChange = { location = it },
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
                    },
                    isEditing = true
                )

                TimeSelectorFieldWithOverlay(
                    label = "開始時間",
                    time = startTime,
                    formatter = timeFormatter,
                    onClick = {
                        showTimePickerDialog(context, startTime) { startTime = it }
                    },
                    isEditing = true
                )

                TimeSelectorFieldWithOverlay(
                    label = "結束時間",
                    time = endTime,
                    formatter = timeFormatter,
                    onClick = {
                        showTimePickerDialog(context, endTime) { endTime = it }
                    },
                    isEditing = true
                )


                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = transportation, onValueChange = { transportation = it },
                    label = { Text("交通方式") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = note, onValueChange = { note = it },
                    label = { Text("備註") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            AppExtendedFab(
                onClick = {
                    val isValid = location.isNotBlank() && selectedDate != null && startTime != null && endTime != null
                    if (!isValid || isSaving) {
                        Toast.makeText(context, "請填寫完整欄位", Toast.LENGTH_SHORT).show()
                        return@AppExtendedFab
                    }

                    isSaving = true

                    val dayIndex = ChronoUnit.DAYS.between(tripStartDate, selectedDate).toInt() + 1

                    val scheduleItem = ScheduleItem(
                        day = dayIndex,
                        time = ScheduleTime(
                            start = startTime!!.format(timeFormatter),
                            end = endTime!!.format(timeFormatter)
                        ),
                        activity = location,
                        transportation = transportation,
                        note = note
                    )

                    viewModel.submitScheduleItemSafely(travelId, scheduleItem) { success ->
                        isSaving = false
                        Toast.makeText(context, if (success) "新增成功！" else "新增失敗", Toast.LENGTH_SHORT).show()
                        if (success) {
                            onScheduleAdded(scheduleItem)
                            onDismiss()
                        }
                    }
                },
                text = "Done"
            )
        },
        dismissButton = {
            AppExtendedFab(onClick = onDismiss, text = "Cancel")
        }
    )
}

package com.example.myapplication.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var location by remember { mutableStateOf("") }
    var transportation by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf<LocalTime?>(null) }
    var endTime by remember { mutableStateOf<LocalTime?>(null) }

    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
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
        title = { Text("新增行程") },
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
                    formatter = formatter,
                    onClick = {
                        showTimePicker(startTime) { selected -> startTime = selected }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TimeSelectorFieldWithOverlay(
                    label = "結束時間",
                    time = endTime,
                    formatter = formatter,
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

                        val scheduleItem = ScheduleItem(
                            day = dayIndex,
                            time = ScheduleTime(
                                start = startTime!!.format(formatter),
                                end = endTime!!.format(formatter)
                            ),
                            activity = location,
                            transportation = transportation,
                            note = note
                        )

                        viewModel.submitScheduleItem(travelId, scheduleItem) { success ->
                            if (success) {
                                Toast.makeText(context, "新增成功！", Toast.LENGTH_SHORT).show()
                                onScheduleAdded(scheduleItem)
                                onDismiss()
                            } else {
                                Toast.makeText(context, "新增失敗", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    text = "Done"
                )
            }
        },
        dismissButton = {
            AppExtendedFab(onClick = onDismiss, text = "Cancel")
        }
    )
}

@Composable
fun TimeSelectorFieldWithOverlay(
    label: String,
    time: LocalTime?,
    formatter: DateTimeFormatter,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = time?.format(formatter) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}

fun showDatePicker(
    context: Context,
    startDate: LocalDate,
    endDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val now = LocalDate.now()
    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        now.year,
        now.monthValue - 1,
        now.dayOfMonth
    )

    // 設定可選擇的最小與最大日期
    dialog.datePicker.minDate = startDate.toEpochDay() * 24 * 60 * 60 * 1000
    dialog.datePicker.maxDate = endDate.toEpochDay() * 24 * 60 * 60 * 1000

    dialog.show()
}

@Composable
fun DateSelectorFieldWithOverlay(
    label: String,
    date: LocalDate?,
    formatter: DateTimeFormatter,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = date?.format(formatter) ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { onClick() }
        )
    }
}
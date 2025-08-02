package com.example.myapplication.ui.components.dialogs

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.ScheduleTime
import com.example.myapplication.ui.components.fields.DateSelectorFieldWithOverlay
import com.example.myapplication.ui.components.fields.TimeSelectorFieldWithOverlay
import com.example.myapplication.ui.components.picker.showTimePickerDialog
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
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
    val context = LocalContext.current

    var isEditing by remember { mutableStateOf(false) } // 👈 狀態控制

    var selectedDate by remember { mutableStateOf(tripStartDate.plusDays((originalItem.day - 1).toLong())) }
    var location by remember { mutableStateOf(originalItem.placeName) }
    var transportation by remember { mutableStateOf(originalItem.transportation) }
    var note by remember { mutableStateOf(originalItem.note ?: "") }
    var startTime by remember { mutableStateOf(originalItem.startTime) }
    var endTime by remember { mutableStateOf(originalItem.endTime) }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.height(680.dp),
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                // ⬆️ Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                    Text(
                        text = "Edit Schedule",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(onClick = {
                        if (isEditing) {
                            val updatedItem = ScheduleItem(
                                day = ChronoUnit.DAYS.between(tripStartDate, selectedDate).toInt() + 1,
                                time = ScheduleTime(
                                    start = startTime?.format(timeFormatter) ?: "",
                                    end = endTime?.format(timeFormatter) ?: ""
                                ),
                                placeName = location,
                                transportation = transportation,
                                note = note
                            )
                            onScheduleEdited(updatedItem)
                            onDismiss()
                        } else {
                            isEditing = true
                        }
                    }) {
                        Text(if (isEditing) "Save" else "Edit")
                    }
                }

                // ⬇️ Form content
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Spacer(Modifier.height(8.dp))
                    DateSelectorFieldWithOverlay(
                        label = "Date",
                        date = selectedDate,
                        formatter = dateFormatter,
                        onClick = {
                            if (isEditing) {
                                showDatePickerDialog(context, selectedDate, tripStartDate, tripEndDate) {
                                    selectedDate = it
                                }
                            }
                        },
                        isEditing = isEditing
                    )

                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = !isEditing // ✅ 改這裡
                    )


                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = transportation,
                        onValueChange = { transportation = it },
                        label = { Text("Transportation") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = !isEditing
                    )

                    Spacer(Modifier.height(16.dp))
                    TimeSelectorFieldWithOverlay(
                        label = "Start Time",
                        time = startTime,
                        formatter = timeFormatter,
                        onClick = {
                            if (isEditing) {
                                showTimePickerDialog(context, startTime) { selected ->
                                    startTime = selected
                                }
                            }
                        },
                        isEditing = isEditing
                    )

                    Spacer(Modifier.height(16.dp))
                    TimeSelectorFieldWithOverlay(
                        label = "End Time",
                        time = endTime,
                        formatter = timeFormatter,
                        onClick = {
                            if (isEditing) {
                                showTimePickerDialog(context, endTime) { selected ->
                                    endTime = selected
                                }
                            }
                        },
                        isEditing = isEditing
                    )

                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = !isEditing
                    )
                }
            }
        }
    }
}

fun showDatePickerDialog(
    context: Context,
    initialDate: LocalDate?,
    startDate: LocalDate,
    endDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val now = LocalDate.now()
    val picker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select date")
        .setSelection((initialDate ?: now).toEpochDay() * 24 * 60 * 60 * 1000)
        .build()

    picker.addOnPositiveButtonClickListener { millis ->
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
            onDateSelected(date)
        }
    }

    picker.show((context as AppCompatActivity).supportFragmentManager, picker.toString())
}

package com.example.myapplication.ui.screens.b_myplans.e_addPlace

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.ScheduleTime
import com.example.myapplication.data.model.Travel
import com.example.myapplication.ui.components.bar.OverlayScreenWithCloseIcon
import com.example.myapplication.ui.components.placedetaildialog.comp.ImgSection
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun AddScheduleScreen(
    currentTrip: Travel,
    navController: NavController,
    attraction: Attraction?,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var arrivalTime by remember { mutableStateOf<LocalTime?>(null) }
    var departureTime by remember { mutableStateOf<LocalTime?>(null) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(currentTrip.startDate, formatter)
    val endDate = LocalDate.parse(currentTrip.endDate, formatter)


    OverlayScreenWithCloseIcon(
        onClose = { navController.popBackStack() },
        title = "新增行程點"
    ) {
        if (attraction == null) {
            Text("未提供景點資訊")
            return@OverlayScreenWithCloseIcon
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            attraction.imageUrl?.let { ImgSection(imageUrl = it) }

            Text(
                text = attraction.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = listOfNotNull(
                    attraction.address,
                    "Open 24 hours",
                    "A luxurious hotel with stunning city views and top-notch amenities."
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            DateSelector(
                label = "選擇日期",
                selectedDate = selectedDate,
                startDate = startDate,
                endDate = endDate,
                onDateSelected = { selectedDate = it }
            )


            TimeSelector(
                label = "Arrival Time",
                selectedTime = arrivalTime,
                onTimeSelected = { arrivalTime = it }
            )

            TimeSelector(
                label = "Departure Time",
                selectedTime = departureTime,
                onTimeSelected = { departureTime = it }
            )

            // ➕ 行程加入按鈕
            Button(
                onClick = {
                    if (selectedDate != null && arrivalTime != null && departureTime != null) {
                        val dayIndex = ChronoUnit.DAYS.between(startDate, selectedDate).toInt() + 1

                        val scheduleItem = ScheduleItem(
                            day = dayIndex,
                            time = ScheduleTime(
                                start = arrivalTime!!.format(DateTimeFormatter.ofPattern("HH:mm")),
                                end = departureTime!!.format(DateTimeFormatter.ofPattern("HH:mm"))
                            ),
                            transportation = "",
                            note = "",
                            placeId = attraction.id,
                            placeName = attraction.name,
                            photoReference = attraction.imageUrl
                        )

                        viewModel.submitScheduleItemSafely(
                            travelId = currentTrip._id,
                            item = scheduleItem,
                            onResult = {
                                navController.popBackStack()
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedDate != null && arrivalTime != null && departureTime != null
            ) {
                Icon(Icons.Default.Place, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to Itinerary")
            }
        }
    }
}


@Composable
fun DateSelector(
    label: String,
    selectedDate: LocalDate?,
    startDate: LocalDate,
    endDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .clickable {
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(label)
                    .setSelection(
                        (selectedDate?.toEpochDay()?.times(24 * 60 * 60 * 1000))
                            ?: (startDate.toEpochDay() * 24 * 60 * 60 * 1000)
                    )
                    .build()

                datePicker.addOnPositiveButtonClickListener { timeInMillis ->
                    val pickedDate = Instant.ofEpochMilli(timeInMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()

                    if (!pickedDate.isBefore(startDate) && !pickedDate.isAfter(endDate)) {
                        onDateSelected(pickedDate)
                    } else {
                        Toast.makeText(context, "只能選擇旅程期間的日期", Toast.LENGTH_SHORT).show()
                    }
                }

                datePicker.show((context as AppCompatActivity).supportFragmentManager, "DatePicker")
            }
    ) {
        Text(
            text = selectedDate?.toString() ?: label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
fun TimeSelector(
    label: String,
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .clickable {
                val now = LocalTime.now()
                val dialog = TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        onTimeSelected(LocalTime.of(hour, minute))
                    },
                    selectedTime?.hour ?: now.hour,
                    selectedTime?.minute ?: now.minute,
                    true
                )
                dialog.show()
            }
    ) {
        Text(
            text = selectedTime?.format(timeFormatter) ?: label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

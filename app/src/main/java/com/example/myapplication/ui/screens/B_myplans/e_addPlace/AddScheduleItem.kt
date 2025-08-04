package com.example.myapplication.ui.screens.b_myplans.e_addPlace

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun AddScheduleScreen(
    currentTrip: Travel,
    navController: NavController,
    attraction: Attraction?,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    var selectedDay by remember { mutableStateOf<Int?>(null) }
    var arrivalTime by remember { mutableStateOf<LocalTime?>(null) }
    var departureTime by remember { mutableStateOf<LocalTime?>(null) }

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

            DaySelector(
                totalDays = currentTrip.days,
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it }
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
                    if (selectedDay != null && arrivalTime != null && departureTime != null) {
                        val scheduleItem = ScheduleItem(
                            day = selectedDay!!,
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
                enabled = selectedDay != null && arrivalTime != null && departureTime != null
            ) {
                Icon(Icons.Default.Place, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add to Itinerary")
            }
        }
    }
}


@Composable
fun DaySelector(
    totalDays: Int,
    selectedDay: Int?,
    onDaySelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .clickable { expanded = true }
    ) {
        Text(
            text = selectedDay?.let { "Day $it" } ?: "Select Day",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            for (day in 1..totalDays) {
                DropdownMenuItem(
                    text = { Text("Day $day") },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    }
                )
            }
        }
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

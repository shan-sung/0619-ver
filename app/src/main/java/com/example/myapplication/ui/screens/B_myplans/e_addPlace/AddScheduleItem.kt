package com.example.myapplication.ui.screens.b_myplans.e_addPlace

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.data.model.PlaceInfo
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.data.model.ScheduleTime
import com.example.myapplication.data.model.SourceType
import com.example.myapplication.data.model.Travel
import com.example.myapplication.ui.components.DateSelector
import com.example.myapplication.ui.components.TimeSelector
import com.example.myapplication.ui.components.bar.OverlayScreenWithCloseIcon
import com.example.myapplication.ui.components.placedetaildialog.comp.ImgSection
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel
import java.time.LocalDate
import java.time.LocalTime
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
    var showDatePicker by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(currentTrip.startDate, formatter)
    val endDate = LocalDate.parse(currentTrip.endDate, formatter)

    OverlayScreenWithCloseIcon(
        onClose = { navController.popBackStack() },
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

            // ✅ 日期選擇顯示元件
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
                    .clickable { showDatePicker = true }
            ) {
                Text(
                    text = selectedDate?.toString() ?: "選擇日期",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (showDatePicker) {
                DateSelector(
                    selectedDate = selectedDate,
                    startDate = startDate,
                    endDate = endDate,
                    onDateSelected = {
                        selectedDate = it
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false }
                )
            }

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

            Log.d("Check", "userRatingsTotal = ${attraction.userRatingsTotal}")

            // ➕ 行程加入按鈕
            Button(
                onClick = {
                    if (selectedDate != null && arrivalTime != null && departureTime != null) {
                        val dayIndex = ChronoUnit.DAYS.between(startDate, selectedDate).toInt() + 1

                        val place = PlaceInfo(
                            source = SourceType.GOOGLE,
                            id = attraction.id,
                            name = attraction.name,
                            address = attraction.address,
                            lat = attraction.lat,
                            lng = attraction.lng,
                            imageUrl = attraction.imageUrl,
                            rating = attraction.rating,
                            userRatingsTotal = attraction.userRatingsTotal
                        )

                        val scheduleItem = ScheduleItem(
                            day = dayIndex,
                            time = ScheduleTime(
                                start = arrivalTime!!.format(DateTimeFormatter.ofPattern("HH:mm")),
                                end = departureTime!!.format(DateTimeFormatter.ofPattern("HH:mm"))
                            ),
                            transportation = "",
                            note = "",
                            place = place
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
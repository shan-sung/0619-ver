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
import androidx.compose.material.icons.filled.Edit
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
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.TimeSelector
import com.example.myapplication.ui.components.bar.OverlayScreenWithCloseIcon
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.ImgSection
import com.example.myapplication.ui.components.inputfield.DateSelector
import com.example.myapplication.viewmodel.TripDetailViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun ScheduleForm(
    attraction: Attraction,
    startDate: LocalDate,
    endDate: LocalDate,
    initialDate: LocalDate?,
    initialArrival: LocalTime?,
    initialDeparture: LocalTime?,
    onSubmit: (selectedDate: LocalDate, arrivalTime: LocalTime, departureTime: LocalTime) -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    var arrivalTime by remember { mutableStateOf(initialArrival) }
    var departureTime by remember { mutableStateOf(initialDeparture) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        attraction.imageUrl?.let { ImgSection(imageUrl = it) }

        Text(text = attraction.name, style = MaterialTheme.typography.titleLarge)

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

        TimeSelector(label = "Arrival Time", selectedTime = arrivalTime) { arrivalTime = it }
        TimeSelector(label = "Departure Time", selectedTime = departureTime) { departureTime = it }

        Button(
            onClick = {
                if (selectedDate != null && arrivalTime != null && departureTime != null) {
                    onSubmit(selectedDate!!, arrivalTime!!, departureTime!!)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedDate != null && arrivalTime != null && departureTime != null
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("儲存變更")
        }
    }
}


@Composable
fun AddScheduleScreen(
    currentTrip: Travel,
    navController: NavController,
    attraction: Attraction,
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(currentTrip.startDate, formatter)
    val endDate = LocalDate.parse(currentTrip.endDate, formatter)

    OverlayScreenWithCloseIcon(onClose = { navController.popBackStack() }) {
        ScheduleForm(
            attraction = attraction,
            startDate = startDate,
            endDate = endDate,
            initialDate = null,
            initialArrival = null,
            initialDeparture = null
        ) { selectedDate, arrivalTime, departureTime ->

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
                userRatingsTotal = attraction.userRatingsTotal,
                openingHours = attraction.openingHours
            )

            val scheduleItem = ScheduleItem(
                day = dayIndex,
                time = ScheduleTime(
                    start = arrivalTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    end = departureTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                ),
                transportation = "",
                note = "",
                place = place
            )

            viewModel.submitScheduleItemSafely(
                travelId = currentTrip._id,
                item = scheduleItem,
                onResult = { success ->
                    if (success) {
                        navController.navigate(Routes.MyPlans.detailRoute(currentTrip._id, dayIndex)) {
                            popUpTo(Routes.MyPlans.MAIN)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun EditScheduleScreen(
    currentTrip: Travel,
    navController: NavController,
    scheduleItem: ScheduleItem,
    itemIndex: Int,  // ✅ 新增：你要編輯的行程項目在當日中的 index
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(currentTrip.startDate, formatter)
    val endDate = LocalDate.parse(currentTrip.endDate, formatter)
    val place = scheduleItem.place

    if (place.id == null || place.name == null) {
        // 可根據需求給提示、回前頁、throw exception 等
        Log.e("EditScheduleScreen", "Place id or name is null")
        navController.popBackStack()
        return
    }
    val attraction = Attraction(
        id = place.id,
        name = place.name,
        address = place.address,
        lat = place.lat,
        lng = place.lng,
        imageUrl = place.imageUrl,
        rating = place.rating,
        userRatingsTotal = place.userRatingsTotal,
        openingHours = place.openingHours
    )

    val initialDate = startDate.plusDays((scheduleItem.day - 1).toLong())
    val initialArrival = LocalTime.parse(scheduleItem.time.start, DateTimeFormatter.ofPattern("HH:mm"))
    val initialDeparture = LocalTime.parse(scheduleItem.time.end, DateTimeFormatter.ofPattern("HH:mm"))

    OverlayScreenWithCloseIcon(onClose = { navController.popBackStack() }) {
        ScheduleForm(
            attraction = attraction,
            startDate = startDate,
            endDate = endDate,
            initialDate = initialDate,
            initialArrival = initialArrival,
            initialDeparture = initialDeparture
        ) { selectedDate, arrivalTime, departureTime ->

            val updatedDay = ChronoUnit.DAYS.between(startDate, selectedDate).toInt() + 1

            val updatedItem = scheduleItem.copy(
                day = updatedDay,
                time = ScheduleTime(
                    start = arrivalTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    end = departureTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                )
            )

            viewModel.updateScheduleItemAndRefresh(
                travelId = currentTrip._id ?: return@ScheduleForm,
                day = scheduleItem.day,
                index = itemIndex,
                updatedItem = updatedItem,
                onResult = { success ->
                    if (success) {
                        navController.navigate(Routes.MyPlans.detailRoute(currentTrip._id!!, updatedDay)) {
                            popUpTo(Routes.MyPlans.MAIN)
                        }
                    }
                }
            )
        }
    }
}

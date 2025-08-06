package com.example.myapplication.ui.components.dialogs

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.myapplication.ui.components.inputfield.DateSelector
import com.example.myapplication.ui.components.inputfield.TextInputField
import com.example.myapplication.ui.components.TimeSelector
import com.example.myapplication.viewmodel.TripDetailViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun AddPlaceDialog(
    currentTrip: Travel,
    navController: NavController,
    viewModel: TripDetailViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var arrivalTime by remember { mutableStateOf<LocalTime?>(null) }
    var departureTime by remember { mutableStateOf<LocalTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(currentTrip.startDate, formatter)
    val endDate = LocalDate.parse(currentTrip.endDate, formatter)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增自訂地點") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TextInputField(
                    label = "地點名稱",
                    text = name,
                    onTextChange = { name = it }
                )

                TextInputField(
                    label = "地址",
                    text = address,
                    onTextChange = { address = it }
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
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
                    label = "抵達時間",
                    selectedTime = arrivalTime,
                    onTimeSelected = { arrivalTime = it }
                )

                TimeSelector(
                    label = "離開時間",
                    selectedTime = departureTime,
                    onTimeSelected = { departureTime = it }
                )
            }
        },
        confirmButton = {
            val isValid = name.isNotBlank() && selectedDate != null && arrivalTime != null && departureTime != null
            TextButton(
                onClick = {
                    val dayIndex = ChronoUnit.DAYS.between(startDate, selectedDate).toInt() + 1

                    val place = PlaceInfo(
                        source = SourceType.CUSTOM,
                        name = name,
                        address = address.ifBlank { null }
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
                            navController.navigate(Routes.MyPlans.detailRoute(currentTrip._id, dayIndex)) {
                                popUpTo(Routes.MyPlans.MAIN)
                            }
                        }
                    )
                },
                enabled = isValid
            ) {
                Text("加入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun AddGooglePlaceDialog(
    currentTrip: Travel,
    attraction: Attraction,
    navController: NavController,
    viewModel: TripDetailViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(currentTrip.startDate, formatter)
    val endDate = LocalDate.parse(currentTrip.endDate, formatter)

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var arrivalTime by remember { mutableStateOf<LocalTime?>(null) }
    var departureTime by remember { mutableStateOf<LocalTime?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增景點：${attraction.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
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

                TimeSelector(label = "抵達時間", selectedTime = arrivalTime) { arrivalTime = it }
                TimeSelector(label = "離開時間", selectedTime = departureTime) { departureTime = it }
            }
        },
        confirmButton = {
            val isValid = selectedDate != null && arrivalTime != null && departureTime != null
            TextButton(
                onClick = {
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
                            navController.navigate(Routes.MyPlans.detailRoute(currentTrip._id, dayIndex)) {
                                popUpTo(Routes.MyPlans.MAIN)
                            }
                        }
                    )
                },
                enabled = isValid
            ) {
                Text("加入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun EditScheduleDialog(
    currentTrip: Travel,
    scheduleItem: ScheduleItem,
    itemIndex: Int,
    viewModel: TripDetailViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = LocalDate.parse(currentTrip.startDate, formatter)
    val endDate = LocalDate.parse(currentTrip.endDate, formatter)

    val place = scheduleItem.place
    if (place.name.isBlank()) {
        Log.e("EditScheduleDialog", "Place name is missing")
        onDismiss()
        return
    }


    val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
    val initialDate = startDate.plusDays((scheduleItem.day - 1).toLong())
    val initialArrival = LocalTime.parse(scheduleItem.time.start, formatterTime)
    val initialDeparture = LocalTime.parse(scheduleItem.time.end, formatterTime)

    var selectedDate by remember { mutableStateOf(initialDate) }
    var arrivalTime by remember { mutableStateOf(initialArrival) }
    var departureTime by remember { mutableStateOf(initialDeparture) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("編輯景點：${place.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .clickable { showDatePicker = true }
                ) {
                    Text(
                        text = selectedDate.toString(),
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

                TimeSelector(label = "抵達時間", selectedTime = arrivalTime) { arrivalTime = it }
                TimeSelector(label = "離開時間", selectedTime = departureTime) { departureTime = it }
            }
        },
        confirmButton = {
            val isValid = selectedDate != null && arrivalTime != null && departureTime != null
            TextButton(
                onClick = {
                    val updatedDay = ChronoUnit.DAYS.between(startDate, selectedDate).toInt() + 1
                    val updatedItem = scheduleItem.copy(
                        day = updatedDay,
                        time = ScheduleTime(
                            start = arrivalTime.format(formatterTime),
                            end = departureTime.format(formatterTime)
                        )
                    )

                    viewModel.updateScheduleItemAndRefresh(
                        travelId = currentTrip._id ?: return@TextButton,
                        day = scheduleItem.day,
                        index = itemIndex,
                        updatedItem = updatedItem,
                        onResult = { success ->
                            if (success) {
                                onDismiss() // ✅ 不再 navigate，改為關閉 Dialog
                            }
                        }
                    )
                },
                enabled = isValid
            ) {
                Text("儲存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

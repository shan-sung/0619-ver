package com.example.myapplication.ui.screens.b_myplans.c_itinerary.elemtent

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.ItineraryDay
import com.example.myapplication.ui.components.ScheduleTimeline
import androidx.navigation.NavController


@Composable
fun DayContent(
    dayIndex: Int,
    itineraryDay: ItineraryDay?,
    dateOverride: String,
    travelId: String,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = dateOverride,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        if (itineraryDay != null) {
            ScheduleTimeline(
                schedule = itineraryDay.schedule,
                travelId = travelId,
                navController = navController,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(0.dp)
            )
        } else {
            Text("尚無行程資料")
        }
    }
}

package com.example.myapplication.ui.screens.myplans.trip.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.model.ItineraryDay
import com.example.myapplication.ui.components.ScheduleTimeline

@Composable
fun DayContent(
    dayIndex: Int,
    itineraryDay: ItineraryDay?,
    dateOverride: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Day ${dayIndex + 1} - $dateOverride",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (itineraryDay != null) {
            ScheduleTimeline(
                schedule = itineraryDay.schedule,
                modifier = Modifier.weight(1f, fill = false)
            )
        } else {
            Text("尚無行程資料")
        }
    }
}
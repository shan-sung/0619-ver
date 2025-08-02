package com.example.myapplication.ui.screens.b_myplans.b_prev.survey

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.DateRangePickerModal
import com.example.myapplication.ui.components.QuesText
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DateRangeScreen(
    startDate: LocalDate?,
    endDate: LocalDate?,
    onDateRangeChange: (LocalDate?, LocalDate?) -> Unit
) {
    var showRangeModal by rememberSaveable { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    val rangeText = if (startDate != null && endDate != null) {
        "${startDate.format(formatter)} - ${endDate.format(formatter)}"
    } else {
        "Click to select range"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        QuesText("起迄日期")

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = rangeText,
                onValueChange = {},
                readOnly = true,
                label = { Text("Trip Date Range") },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        Log.d("DATE_PICKER", "TextField clicked")
                        showRangeModal = true
                    }
            )
        }

        if (showRangeModal) {
            DateRangePickerModal(
                onDateRangeSelected = { (startMillis, endMillis) ->
                    val start = startMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    val end = endMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    onDateRangeChange(start, end)
                    showRangeModal = false
                },
                onDismiss = { showRangeModal = false }
            )
        }
    }
}

package com.example.myapplication.ui.screens.myplans.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.QuesText

@Composable
fun DailyHourRangeScreen(
    startHour: Int,
    endHour: Int,
    onChange: (Int, Int) -> Unit
) {
    val floatRangeSaver = Saver<ClosedFloatingPointRange<Float>, List<Float>>(
        save = { listOf(it.start, it.endInclusive) },
        restore = { it[0]..it[1] }
    )

    var sliderRange by rememberSaveable(stateSaver = floatRangeSaver) {
        mutableStateOf(startHour.toFloat()..endHour.toFloat())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuesText("希望每天的行程安排時間是？")
        Text("行程時間：${sliderRange.start.toInt()} 點 ～ ${sliderRange.endInclusive.toInt()} 點")

        RangeSlider(
            value = sliderRange,
            onValueChange = { range ->
                val coercedStart = range.start.coerceAtMost(range.endInclusive - 1f)
                val coercedEnd = range.endInclusive.coerceAtLeast(range.start + 1f)
                sliderRange = coercedStart..coercedEnd
            },
            onValueChangeFinished = {
                onChange(sliderRange.start.toInt(), sliderRange.endInclusive.toInt())
            },
            valueRange = 0f..24f,
            steps = 23
        )
    }
}
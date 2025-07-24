package com.example.myapplication.ui.screens.myplans.creation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.QuesText

@Composable
fun AgeRangeScreen(
    value: String = "",
    onValueChange: (String) -> Unit
) {
    val ageRanges = listOf(
        "Under 18",
        "18–24",
        "25–34",
        "35–44",
        "45–54",
        "55–64",
        "65+"
    )

    var selectedRange by rememberSaveable { mutableStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuesText("平均年齡")

        ageRanges.forEach { range ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedRange = range
                        onValueChange(range)
                    }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedRange == range,
                    onClick = {
                        selectedRange = range
                        onValueChange(range)
                    }
                )
                Text(text = range)
            }
        }
    }
}

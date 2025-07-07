package com.example.myapplication.ui.screens.creation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
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
fun TransportOptionsScreen(
    selected: List<String> = emptyList(),
    onChange: (List<String>) -> Unit
) {
    val options = listOf("Walking", "Bicycle", "Car", "Motorbike", "Bus", "Train", "Boat", "Plane")
    var selectedTransports by rememberSaveable { mutableStateOf(selected) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        QuesText("Which transport options are acceptable during your trip?")

        options.forEach { option ->
            val isSelected = selectedTransports.contains(option)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedTransports = if (isSelected) {
                            selectedTransports - option
                        } else {
                            selectedTransports + option
                        }
                        onChange(selectedTransports)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = {
                        selectedTransports = if (it) {
                            selectedTransports + option
                        } else {
                            selectedTransports - option
                        }
                        onChange(selectedTransports)
                    }
                )
                Text(option)
            }
        }
    }
}

package com.example.myapplication.ui.screens.myplans.creation

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
fun PreferencesScreen(
    selected: List<String> = emptyList(),
    onChange: (List<String>) -> Unit
) {
    val options = listOf("Nature", "History", "Food", "Shopping", "Adventure", "Relax", "Nightlife")
    var selectedPrefs by rememberSaveable { mutableStateOf(selected) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        QuesText("What are your travel preferences?")

        options.forEach { option ->
            val isSelected = selectedPrefs.contains(option)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedPrefs = if (isSelected) {
                            selectedPrefs - option
                        } else {
                            selectedPrefs + option
                        }
                        onChange(selectedPrefs)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = {
                        selectedPrefs = if (it) {
                            selectedPrefs + option
                        } else {
                            selectedPrefs - option
                        }
                        onChange(selectedPrefs)
                    }
                )
                Text(option)
            }
        }
    }
}

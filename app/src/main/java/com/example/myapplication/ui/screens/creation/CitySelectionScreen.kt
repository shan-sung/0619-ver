package com.example.myapplication.ui.screens.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun CitySelectionScreen(
    selected: List<String> = emptyList(),
    onChange: (List<String>) -> Unit
) {
    val taiwanCities = listOf(
        "台北市", "新北市", "桃園市", "台中市", "台南市", "高雄市",
        "基隆市", "新竹市", "嘉義市",
        "新竹縣", "苗栗縣", "彰化縣", "南投縣", "雲林縣", "嘉義縣",
        "屏東縣", "宜蘭縣", "花蓮縣", "台東縣",
        "澎湖縣", "金門縣", "連江縣"
    )

    var selectedCities by rememberSaveable { mutableStateOf(selected.toList()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        QuesText("Which cities do you want to visit?")

        LazyColumn(
            modifier = Modifier.fillMaxHeight(0.8f)
        ) {
            items(taiwanCities) { city ->
                val isSelected = selectedCities.contains(city)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            selectedCities = if (checked) {
                                selectedCities + city
                            } else {
                                selectedCities - city
                            }
                            onChange(selectedCities)
                        }
                    )
                    Text(city)
                }
            }
        }
    }
}


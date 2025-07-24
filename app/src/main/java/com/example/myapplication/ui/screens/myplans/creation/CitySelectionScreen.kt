package com.example.myapplication.ui.screens.myplans.creation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    val options = listOf(
        "台北市", "新北市", "桃園市", "台中市", "台南市", "高雄市",
        "基隆市", "新竹市", "嘉義市",
        "新竹縣", "苗栗縣", "彰化縣", "南投縣", "雲林縣", "嘉義縣",
        "屏東縣", "宜蘭縣", "花蓮縣", "台東縣",
        "澎湖縣", "金門縣", "連江縣"
    )

    var selectedCities by rememberSaveable { mutableStateOf(selected) }

    Column(
        modifier = Modifier
            .fillMaxSize() // ✅ 注意是 fillMaxSize
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuesText("這次想去哪～")

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 用 LazyColumn 或 Column + verticalScroll 包住 Checkbox 清單
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // ✅ 讓他自動撐滿剩下空間
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(options) { option ->
                val isSelected = selectedCities.contains(option)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedCities = if (isSelected) {
                                selectedCities - option
                            } else {
                                selectedCities + option
                            }
                            onChange(selectedCities)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = {
                            selectedCities = if (it) {
                                selectedCities + option
                            } else {
                                selectedCities - option
                            }
                            onChange(selectedCities)
                        }
                    )
                    Text(option)
                }
            }
        }
    }
}


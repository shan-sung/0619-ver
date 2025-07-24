package com.example.myapplication.ui.screens.myplans.creation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
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
fun GoogleRatingScreen(
    value: Boolean = false,  // ✅ 預設值也改為 Boolean
    onValueChange: (Boolean) -> Unit  // ✅ callback 型別也改掉
) {
    val options = listOf(
        true to "是",
        false to "否"
    )

    var selected by rememberSaveable { mutableStateOf(value) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuesText("是否想參考Google Maps的評分")
        Text(
            text = "提醒：若選擇參考評分，可能會排除熱門但評價較低的景點",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        options.forEach { (boolValue, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selected = boolValue
                        onValueChange(boolValue)
                    }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == boolValue,
                    onClick = {
                        selected = boolValue
                        onValueChange(boolValue)
                    }
                )
                Text(text = label)
            }
        }
    }
}
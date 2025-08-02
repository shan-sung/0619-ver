package com.example.myapplication.ui.screens.b_myplans.b_prev.survey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.AppFab
import com.example.myapplication.ui.components.QuesText

@Composable
fun PeopleCountScreen(count: Int, onCountChange: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        QuesText("這次的旅遊人數")

        Text(
            text = "$count people",
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AppFab(onClick = { if (count > 1) onCountChange(count - 1) }, icon = Icons.Filled.Remove, contentDescription = "Remove")
            AppFab(onClick = { onCountChange(count + 1) },icon = Icons.Filled.Add, contentDescription = "Add")
        }
    }
}

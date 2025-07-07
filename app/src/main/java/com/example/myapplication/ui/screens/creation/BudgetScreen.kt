package com.example.myapplication.ui.screens.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
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
fun BudgetScreen(
    initialBudget: Int = 10000,
    onChange: (Int) -> Unit
) {
    var budget by rememberSaveable { mutableStateOf(initialBudget) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        QuesText("What is your total budget for this trip?")

        Text("NT$${budget}", style = MaterialTheme.typography.titleLarge)

        Slider(
            value = budget.toFloat(),
            onValueChange = {
                budget = it.toInt()
                onChange(budget)
            },
            valueRange = 1000f..50000f,
            steps = 48, // 每 1000 一格
            modifier = Modifier.fillMaxWidth()
        )
    }
}
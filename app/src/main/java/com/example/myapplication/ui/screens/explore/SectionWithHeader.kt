package com.example.myapplication.ui.screens.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.components.SectionHeader

@Composable
fun SectionWithHeader(title: String, onMoreClick: () -> Unit, content: @Composable () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(title)
            TextButton(onClick = onMoreClick) {
                Text("查看更多")
            }
        }
        content()
    }
}

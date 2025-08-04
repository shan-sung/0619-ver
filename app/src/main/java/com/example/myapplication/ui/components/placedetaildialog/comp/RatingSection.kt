package com.example.myapplication.ui.components.placedetaildialog.comp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RatingSection(rating: Double, totalReviews: Int) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // ⭐ 顯示總評分 + 展開箭頭
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                String.format("%.1f", rating),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "$totalReviews 則評價",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        // 📊 展開的評分分佈列
        AnimatedVisibility(visible = expanded) {
            Column {
                Spacer(Modifier.height(4.dp))
                (5 downTo 1).forEach { stars ->
                    val percent = when (stars) {
                        5 -> 0.4f
                        4 -> 0.3f
                        3 -> 0.15f
                        2 -> 0.1f
                        else -> 0.05f
                    }
                    RatingBar(stars, percent)
                }
            }
        }
    }
}


@Composable
fun RatingBar(stars: Int, percent: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$stars", modifier = Modifier.width(16.dp))
        Icon(Icons
            .Default.Star,
            null,
            Modifier.size(16.dp),
            tint = Color(0xFFFFC107)
        )
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp).height(6.dp),
            color = Color(0xFFFFC107)
        )
    }
}
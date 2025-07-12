package com.example.myapplication.ui.screens.saved

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.model.Attraction
import com.example.myapplication.viewmodel.SavedViewModel

@Composable
fun SavedScreen(
    savedViewModel: SavedViewModel,
    onItemClick: (Attraction) -> Unit
) {
    val savedAttractions by savedViewModel.savedAttractions.collectAsState()

    // 僅在畫面建立時呼叫一次
    LaunchedEffect(Unit) {
        savedViewModel.fetchSavedAttractions()
    }

    if (savedAttractions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "尚未收藏任何景點",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(savedAttractions, key = { it.id }) { attraction ->
                SavedAttractionItem(
                    attraction = attraction,
                    onClick = { onItemClick(attraction) },
                    onRemoveFromSaved = { toRemove ->
                        savedViewModel.removeFromSaved(toRemove)
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun SavedAttractionItem(
    attraction: Attraction,
    onClick: () -> Unit,
    onRemoveFromSaved: (Attraction) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = attraction.imageUrl ?: "https://via.placeholder.com/150",
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f) // 推擠愛心到右側
        ) {
            Text(
                text = attraction.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = attraction.category,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        IconButton(onClick = { onRemoveFromSaved(attraction) }) {
            Icon(
                imageVector = Icons.Filled.Favorite, // 預設已儲存
                contentDescription = "Remove from saved",
                tint = Color.Red
            )
        }
    }
}
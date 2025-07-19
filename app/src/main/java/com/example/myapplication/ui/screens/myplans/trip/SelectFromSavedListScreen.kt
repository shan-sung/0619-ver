package com.example.myapplication.ui.screens.myplans.trip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.model.Attraction
import com.example.myapplication.viewmodel.saved.SavedViewModel

@Composable
fun SelectFromSavedListScreen(
    onSelect: (Attraction) -> Unit,
    viewModel: SavedViewModel = hiltViewModel(),
    navController: NavController  // ✅ 加入 NavController
) {
    LaunchedEffect(Unit) {
        viewModel.fetchSavedAttractions()
    }

    val savedList by viewModel.savedAttractions.collectAsState()

    if (savedList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No saved attractions found.")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(savedList) { attraction ->
                SavedAttractionItem(
                    attraction = attraction,
                    onClick = {
                        // ✅ 存進 previousBackStackEntry 的 savedStateHandle
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_attraction", attraction)
                        navController.popBackStack() // 返回上一頁
                    },
                    onRemoveFromSaved = { viewModel.removeFromSaved(it) }
                )
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
    val typeMap = mapOf(
        "nature" to "自然",
        "culture" to "文化",
        "food" to "美食",
        "shopping" to "購物",
        "historical" to "歷史",
        "adventure" to "冒險",
        "relax" to "休閒"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = attraction.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = attraction.tags?.joinToString("、") { typeMap[it] ?: it } ?: "未知類別",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        IconButton(onClick = {
            onRemoveFromSaved(attraction)
        }) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Remove from saved",
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

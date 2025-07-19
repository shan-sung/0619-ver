package com.example.myapplication.ui.screens.explore

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.myapplication.model.Attraction
import com.example.myapplication.ui.components.AttractionInfoCardVertical

fun LazyListScope.recommendedAttractionsSection(
    attractions: List<Attraction>,
    onShuffle: () -> Unit,
    context: Context,
    onItemClick: (Attraction) -> Unit
) {
    stickyHeader {
        SectionHeader(
            title = "Top picks for you",
            actionText = "🎲",
            onActionClick = onShuffle,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        )
    }

    items(attractions) { a ->
        AttractionInfoCardVertical(
            attraction = a,
            context = context,
            onItemClick = onItemClick
        )
    }

}

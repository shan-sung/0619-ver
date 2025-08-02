package com.example.myapplication.ui.screens.b_myplans.e_addPlace.element

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.data.model.Attraction

@Composable
fun AttractionList(
    attractions: List<Attraction>,
    onClick: (Attraction) -> Unit,
    onRemove: (Attraction) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(attractions) { attraction ->
            SavedPlaceItem(
                attraction = attraction,
                onClick = { onClick(attraction) },
                onRemove = onRemove
            )
        }
    }
}
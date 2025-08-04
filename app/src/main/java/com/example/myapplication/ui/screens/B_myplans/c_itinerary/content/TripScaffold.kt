package com.example.myapplication.ui.screens.b_myplans.c_itinerary.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.AppFab

@Composable
fun TripScaffold(
    isOwner: Boolean,
    showBottomSheet: (Boolean) -> Unit,
    showDialog: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }

        if (isOwner) {
            AppFab(
                onClick = { showBottomSheet(true) },
                icon = Icons.Filled.Add,
                contentDescription = "Add Itinerary",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

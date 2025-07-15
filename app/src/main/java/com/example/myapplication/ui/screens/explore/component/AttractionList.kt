package com.example.myapplication.ui.screens.explore.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.myapplication.model.Attraction
import com.example.myapplication.ui.components.InfoCardVertical
import com.example.myapplication.ui.components.toInfoCardData
import com.example.myapplication.viewmodel.saved.SavedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AttractionList(
    attractions: List<Attraction>,
    savedViewModel: SavedViewModel,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    title: String = "你想要做什麼？"
) {
    val context = LocalContext.current
    var selectedAttraction by remember { mutableStateOf<Attraction?>(null) }

    LazyColumn {
        items(attractions) { a ->
            val cardData = remember(a) {
                a.toInfoCardData(context).copy(onClick = { selectedAttraction = a })
            }
            InfoCardVertical(data = cardData)
        }
    }

    selectedAttraction?.let { attraction ->
        AttractionActionDialog(
            attraction = attraction,
            onDismiss = { selectedAttraction = null },
            onSave = {
                savedViewModel.addToSaved(attraction)
                selectedAttraction = null
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("已加入收藏：${attraction.name}")
                }
            },
            onOpenMap = {
                val query = Uri.encode(attraction.name)
                val gmmIntentUri = "geo:0,0?q=$query".toUri()
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                    setPackage("com.google.android.apps.maps")
                }

                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                } else {
                    val webUri = "https://www.google.com/maps/search/?api=1&query=$query".toUri()
                    val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                    context.startActivity(webIntent)
                }

                selectedAttraction = null
            },
            dialogTitle = title
        )
    }
}

@Composable
fun AttractionActionDialog(
    attraction: Attraction,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onOpenMap: () -> Unit,
    dialogTitle: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = { Text(attraction.name) },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("收藏")
            }
        },
        dismissButton = {
            TextButton(onClick = onOpenMap) {
                Text("Google Maps")
            }
        }
    )
}

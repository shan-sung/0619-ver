package com.example.myapplication.ui.components.placedetaildialog.comp

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.myapplication.data.model.Attraction

enum class PlaceActionMode {
    ADD_TO_ITINERARY,
    ADD_TO_FAVORITE,
    REMOVE_FROM_FAVORITE
}

@Composable
fun ActionButtonsRow(
    attraction: Attraction,
    rightButtonLabel: String,
    onRightButtonClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = {
                val gmapUrl = "https://www.google.com/maps/search/?api=1&query=${attraction.name}"
                val intent = Intent(Intent.ACTION_VIEW, gmapUrl.toUri())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("地圖")
        }

        Button(
            onClick = onRightButtonClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(rightButtonLabel)
        }
    }
}
package com.example.myapplication.ui.components.dialogs.placedetaildialog

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.data.model.Attraction
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.ActionButtonsRow
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.ImgSection
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.MapSection
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.OpeningHoursSection
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.PlaceActionMode
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.RatingSection

@Composable
fun PlaceDetailDialog(
    attraction: Attraction?,
    mode: PlaceActionMode,
    onDismiss: () -> Unit,
    onAddToItinerary: () -> Unit = {},
    onRemoveFromFavorite: () -> Unit = {},
    onAddToFavorite: () -> Unit = {}
) {
    if (attraction == null) {
        // 顯示 Loading 或錯誤畫面
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(max = 600.dp),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 76.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    attraction.imageUrl?.let { ImgSection(it) }
                    LaunchedEffect(Unit) {
                        Log.d("PlaceDetail", "Rating: ${attraction.rating}, Total: ${attraction.userRatingsTotal}")
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(attraction.name, style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(attraction.address ?: "", style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(4.dp))
                        attraction.openingHours
                            ?.takeIf { it.isNotEmpty() }
                            ?.let { OpeningHoursSection(it) }
                        attraction.rating?.let {
                            RatingSection(it, attraction.userRatingsTotal ?: 0)
                        }
                        Spacer(Modifier.height(16.dp))
                        MapSection()
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ActionButtonsRow(
                        attraction = attraction,
                        rightButtonLabel = when (mode) {
                            PlaceActionMode.ADD_TO_ITINERARY -> "加入行程"
                            PlaceActionMode.ADD_TO_FAVORITE -> "加入最愛"
                            PlaceActionMode.REMOVE_FROM_FAVORITE -> "移除最愛"
                        },
                        onRightButtonClick = {
                            when (mode) {
                                PlaceActionMode.ADD_TO_ITINERARY -> onAddToItinerary()
                                PlaceActionMode.ADD_TO_FAVORITE -> onAddToFavorite()
                                PlaceActionMode.REMOVE_FROM_FAVORITE -> onRemoveFromFavorite()
                            }
                        }
                    )
                }
            }
        }
    }
}
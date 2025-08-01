package com.example.myapplication.ui.screens.myplans.trip

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import coil.compose.SubcomposeAsyncImage
import com.example.myapplication.model.Attraction

@Composable
fun PlaceDetailDialog(
    attraction: Attraction,
    onDismiss: () -> Unit,
    onAddToItinerary: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 圖片
                attraction.imageUrl?.let {
                    SubcomposeAsyncImage(
                        model = attraction.imageUrl,
                        contentDescription = "Place image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.White)
                            }
                        }
                    )
                }

                Column(modifier = Modifier.padding(20.dp)) {

                    // 📍 景點名稱與地址
                    Text(attraction.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(attraction.address ?: "", style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.height(16.dp))
                    Divider()

                    // 🕒 營業時間
                    attraction.openingHours?.let { hours ->
                        SectionTitle("營業狀態")
                        OpeningHoursSection(hours)
                    }

                    Spacer(Modifier.height(16.dp))

                    // 🗺 地圖
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Map Preview", style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(16.dp))

                    // ⭐ 評分與分佈
                    attraction.rating?.let { rating ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                String.format("%.1f", rating),
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107))
                            Spacer(Modifier.width(8.dp))
                            Text("${attraction.userRatingsTotal ?: 0} 則評價", style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(Modifier.height(8.dp))
                        (5 downTo 1).forEach { stars ->
                            val percent = when (stars) {
                                5 -> 0.4f; 4 -> 0.3f; 3 -> 0.15f; 2 -> 0.1f; else -> 0.05f
                            }
                            RatingBar(stars, percent)
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // 🧭 動作按鈕
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                        ) { Text("地圖") }

                        Button(
                            onClick = onAddToItinerary,
                            modifier = Modifier.weight(1f)
                        ) { Text("加入行程") }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Spacer(Modifier.height(8.dp))
    Text(title, style = MaterialTheme.typography.labelLarge)
    Spacer(Modifier.height(4.dp))
}

@Composable
fun RatingBar(stars: Int, percent: Float) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$stars", modifier = Modifier.width(16.dp))
        Icon(Icons.Default.Star, null, Modifier.size(16.dp), tint = Color(0xFFFFC107))
        LinearProgressIndicator(
            progress = { percent },
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp).height(6.dp),
            color = Color(0xFFFFC107)
        )
    }
}
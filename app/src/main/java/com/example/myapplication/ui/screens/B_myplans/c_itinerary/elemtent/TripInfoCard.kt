package com.example.myapplication.ui.screens.b_myplans.c_itinerary.elemtent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.example.myapplication.data.model.Travel
import com.example.myapplication.navigation.routes.Routes

@Composable
fun TripInfoHeader(
    travel: Travel,
    isInTrip: Boolean,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
    ) {
        // 圖片背景
        SubcomposeAsyncImage(
            model = travel.imageUrl,
            contentDescription = "Trip cover image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize(),
            loading = {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.White)
                }
            }
        )

        // 右上角三顆功能鍵（條件顯示）
        if (isInTrip) {
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Routes.MyPlans.chatRoute(travel._id))
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE0E7FF), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "預算",
                        tint = Color.Black
                    )
                }

                IconButton(
                    onClick = {
                        navController.navigate(Routes.MyPlans.chatRoute(travel._id))
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE0E7FF), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "聊天室",
                        tint = Color.Black
                    )
                }

                IconButton(
                    onClick = {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("show_share_dialog", travel._id)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFE0E7FF), shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "分享行程",
                        tint = Color.Black
                    )
                }
            }
        }

        // 左下角文字敘述
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = travel.title ?: "未命名行程",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = "${travel.members.size}人・${travel.days}天・預算 $${travel.budget ?: 0}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}
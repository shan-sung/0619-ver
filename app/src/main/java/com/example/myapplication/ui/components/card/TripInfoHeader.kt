package com.example.myapplication.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.vector.ImageVector
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
        TripImageBackground(travel.imageUrl)
        if (isInTrip) {
            TripActionButtons(travel._id, navController, Modifier.align(Alignment.TopEnd))
        }
        TripDescription(travel, Modifier.align(Alignment.BottomStart))
    }
}

@Composable
private fun TripImageBackground(imageUrl: String?) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = "Trip cover image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        loading = { ImageLoadingPlaceholder() },
        error = { ImageErrorPlaceholder() }
    )
}

@Composable
private fun ImageLoadingPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun ImageErrorPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.BrokenImage, contentDescription = null, tint = Color.White)
    }
}

@Composable
private fun TripActionButtons(tripId: String, navController: NavController, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ActionIconButton(Icons.Default.MonetizationOn, "預算") {
            navController.navigate(Routes.MyPlans.chatRoute(tripId))
        }
        ActionIconButton(Icons.Default.Chat, "聊天室") {
            navController.navigate(Routes.MyPlans.chatRoute(tripId))
        }
        ActionIconButton(Icons.Default.PersonAdd, "分享行程") {
            navController.currentBackStackEntry?.savedStateHandle?.set("show_share_dialog", tripId)
        }
    }
}

@Composable
private fun ActionIconButton(icon: ImageVector, description: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .background(Color(0xFFE0E7FF), shape = CircleShape)
    ) {
        Icon(icon, contentDescription = description, tint = Color.Black)
    }
}

@Composable
private fun TripDescription(travel: Travel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp)
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

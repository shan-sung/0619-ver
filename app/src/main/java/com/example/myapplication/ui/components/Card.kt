package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.data.model.Travel
import com.example.myapplication.navigation.routes.Routes
import java.util.Locale

@Composable
fun InfoCardVertical(
    travel: Travel,
    navController: NavController,
    showButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    val subtitleParts = listOfNotNull(
        "${travel.members.size} people",
        "${travel.days} days",
        travel.budget?.let { "Budget $${String.format(Locale.US, "%,d", it)}" }
    )
    val subtitle = subtitleParts.joinToString("・")
    val dateRange = "${travel.startDate} 至 ${travel.endDate}"

    Row(
        modifier = modifier
            .clickable {
                navController.navigate(Routes.MyPlans.detailRoute(travel._id))
            }
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = travel.imageUrl?.takeIf { it.isNotBlank() }
                ?: "https://source.unsplash.com/280x160/?nature",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            placeholder = painterResource(id = android.R.drawable.ic_menu_gallery),
            error = painterResource(id = android.R.drawable.ic_menu_gallery)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = travel.title ?: "未命名行程",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (showButton) {
            TextButton(onClick = {
                navController.navigate(Routes.MyPlans.chatRoute(travel._id))
            }) {
                Text("聊天室")
            }
        }
    }
}

@Composable
fun InfoCard(
    travel: Travel,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
    aspectRatio: Float? = null,
    onClick: (() -> Unit)? = null,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    val sizeModifier = when {
        width != null && height != null -> modifier.width(width).height(height)
        width != null -> modifier.width(width)
        height != null -> modifier.height(height)
        aspectRatio != null -> modifier.aspectRatio(aspectRatio)
        else -> modifier
    }

    val imageUrl = travel.imageUrl?.takeIf { it.isNotBlank() }
        ?: "https://source.unsplash.com/280x160/?nature"

    val subtitleParts = listOfNotNull(
        "${travel.members.size} people",
        "${travel.days} days",
        travel.budget?.let { "Budget $${String.format(Locale.US, "%,d", it)}" }
    )
    val subtitle = subtitleParts.joinToString("・")

    Card(
        onClick = { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        modifier = sizeModifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = travel.title ?: "未命名行程",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                buttonText?.let { text ->
                    TextButton(onClick = { onButtonClick?.invoke() }) {
                        Text(text = text, color = Color.White)
                    }
                }
            }
        }
    }
}

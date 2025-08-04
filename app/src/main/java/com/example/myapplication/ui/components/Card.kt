package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.model.Travel
import java.util.Locale

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
    val sizeModifier = modifier.buildSize(width, height, aspectRatio)
    val imageUrl = travel.getImageUrl()
    val subtitle = travel.getSubtitle()

    Card(
        onClick = { onClick?.invoke() }, // if onClick ≠ null, then invoke
        shape = RoundedCornerShape(16.dp),
        modifier = sizeModifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            BackgroundImage(imageUrl)
            BlackOverlay()
            InfoContent(
                title = travel.title ?: "未命名行程",
                subtitle = subtitle,
                buttonText = buttonText,
                onButtonClick = onButtonClick
            )
        }
    }
}

@Composable
private fun Modifier.buildSize(
    width: Dp? = null,
    height: Dp? = null,
    aspectRatio: Float? = null
): Modifier = when {
    width != null && height != null -> this.width(width).height(height)
    width != null -> this.width(width)
    height != null -> this.height(height)
    aspectRatio != null -> this.aspectRatio(aspectRatio)
    else -> this
}


fun Travel.getImageUrl(): String {
    return imageUrl?.takeIf { it.isNotBlank() }
        ?: "https://source.unsplash.com/280x160/?nature"
}

fun Travel.getSubtitle(): String {
    val parts = listOfNotNull(
        "${members.size} 人",
        "$days 天",
        budget?.let { "預算 $${String.format(Locale.US, "%,d", it)}" }
    )
    return parts.joinToString("・")
}

@Composable
private fun BackgroundImage(imageUrl: String) {
    Box{
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
private fun BoxScope.BlackOverlay() {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = 0.2f))
    )
}

@Composable
private fun InfoContent(
    title: String,
    subtitle: String,
    buttonText: String?,
    onButtonClick: (() -> Unit)?
) {
    Box( // 新增一層 Box 讓 align 合法
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                    text = title,
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

            buttonText?.let {
                TextButton(onClick = { onButtonClick?.invoke() }) {
                    Text(text = it, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInfoCard() {
    val dummyTravel = Travel(
        _id = "t1",
        userId = "u1",
        chatRoomId = "c1",
        members = listOf("u1", "u2"),
        created = true,
        title = "台北冒險之旅",
        startDate = "2025-08-15",
        endDate = "2025-08-17",
        budget = 3000,
        description = "這是一趟探索城市文化與美食的旅程。",
        imageUrl = "https://picsum.photos/600/400",
        itinerary = null
    )

    InfoCard(
        travel = dummyTravel,
        width = 300.dp,
        aspectRatio = 4f / 3f,
        onButtonClick = {}
    )
}
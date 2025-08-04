package com.example.myapplication.ui.components.card

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
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

fun Modifier.buildSize(
    width: Dp? = null,
    height: Dp? = null,
    aspectRatio: Float? = null
): Modifier {
    var result = this
    if (width != null) result = result.then(Modifier.width(width))
    if (height != null) result = result.then(Modifier.height(height))
    if (aspectRatio != null) result = result.then(Modifier.aspectRatio(aspectRatio))
    return result
}



const val DEFAULT_TRAVEL_IMAGE_URL = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e"
fun Travel.getImageUrl(): String = imageUrl?.takeIf { it.isNotBlank() } ?: DEFAULT_TRAVEL_IMAGE_URL

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
    Box {
        SubcomposeAsyncImage(
            model = imageUrl,
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
    }
}


@Composable
private fun BoxScope.BlackOverlay(alpha: Float = 0.2f) {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color.Black.copy(alpha = alpha))
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
                .padding(8.dp),
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
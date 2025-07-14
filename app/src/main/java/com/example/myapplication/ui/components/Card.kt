package com.example.myapplication.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.myapplication.model.Attraction
import com.example.myapplication.model.Travel
import com.example.myapplication.navigation.routes.Routes
import java.util.Locale

data class InfoCardData(
    val id: String? = null,
    val title: String,
    val subtitle: String = "",
    val location: String = "",
    val imageUrl: String? = null,
    val mapSearchQuery: String? = null,
    val onClick: (() -> Unit)? = null
)

fun Travel.toInfoCardData(navController: NavController): InfoCardData {
    val subtitleParts = listOfNotNull(
        "${members.size} people",
        "$days days",
        budget?.let { "Budget $${String.format(Locale.US, "%,d", it)}" }
    )
    return InfoCardData(
        id = _id,
        title = title ?: "未命名行程",
        subtitle = subtitleParts.joinToString("・"),
        location = "$startDate 至 $endDate",
        imageUrl = imageUrl,
        onClick = {
            navController.navigate(Routes.MyPlans.detailRoute(_id ?: ""))
        }
    )
}

fun Attraction.toInfoCardData(context: Context): InfoCardData {
    return InfoCardData(
        title = name,
        subtitle = "${rating ?: 0.0} / 5",
        location = city,
        imageUrl = imageUrl,
        mapSearchQuery = name,
        onClick = {
            val query = Uri.encode(name)
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
        }
    )
}

@Composable
fun InfoCardVertical(
    data: InfoCardData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { data.onClick?.invoke() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = data.imageUrl ?: "https://source.unsplash.com/280x160/?nature\n",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(data.title, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(data.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun InfoCard(
    data: InfoCardData,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp? = null,
    aspectRatio: Float? = null,
) {
    val sizeModifier = when {
        width != null && height != null -> modifier.width(width).height(height)
        width != null -> modifier.width(width)
        height != null -> modifier.height(height)
        aspectRatio != null -> modifier.aspectRatio(aspectRatio)
        else -> modifier
    }

    val context = LocalContext.current

    val imagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(data.imageUrl)
            .crossfade(true)
            .build()
    )

    val imageState = imagePainter.state

    Card(
        onClick = { data.onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        modifier = sizeModifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageState is AsyncImagePainter.State.Success) {
                Image(
                    painter = imagePainter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
            } else {
                // 自訂 fallback 畫面
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(MaterialTheme.colorScheme.surface), // 使用主題的 surface 顏色
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "No image",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // 淡遮罩區
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.2f))
            )

            // 底部文字
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

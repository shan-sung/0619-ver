package com.example.myapplication.ui.screens.d_friend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.data.model.UserSummary

@Composable
fun FriendProfileDialog(
    friend: UserSummary,
    alreadyRequested: Boolean,
    isFriend: Boolean, // ✅ 新增這個參數
    onDismiss: () -> Unit,
    onToggleFriendRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.background,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .width(280.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = friend.avatarUrl ?: "https://source.unsplash.com/280x160/?face",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(id = R.drawable.user),
                    error = painterResource(id = R.drawable.user)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(friend.username, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = friend.email ?: "", style = MaterialTheme.typography.bodyMedium)
                Text(text = friend.birthday ?: "", style = MaterialTheme.typography.bodySmall)

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${friend.tripCount ?: 0}", fontWeight = FontWeight.Bold)
                        Text("Trips", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${friend.followerCount ?: 0}", fontWeight = FontWeight.Bold)
                        Text("Followers", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    friend.bio ?: "這個人很神秘，尚未留下自我介紹。",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isFriend) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Already Friends",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                } else {
                    Button(
                        onClick = onToggleFriendRequest,
                        colors = if (alreadyRequested)
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        else
                            ButtonDefaults.buttonColors()
                    ) {
                        Text(if (alreadyRequested) "Cancel Request" else "Add Friend")
                    }
                }
            }
        }
    }
}
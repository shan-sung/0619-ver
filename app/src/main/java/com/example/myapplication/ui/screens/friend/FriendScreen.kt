package com.example.myapplication.ui.screens.friend

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.model.UserSummary
import com.example.myapplication.ui.components.SearchBar
import com.example.myapplication.ui.components.SectionHeader
import com.example.myapplication.util.formatRelativeTime
import com.example.myapplication.viewmodel.friend.FriendViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreen(
    navController: NavController,
    refreshKey: Int
) {
    val viewModel: FriendViewModel = hiltViewModel()

    val friendList by viewModel.friendList.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val pendingRequests by viewModel.pendingRequests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sentRequests by viewModel.sentRequests.collectAsState()

    var showFriendList by remember { mutableStateOf(false) }
    var selectedFriend by remember { mutableStateOf<UserSummary?>(null) }
    var query by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val friendIdSet = remember(friendList) { friendList.map { it.id }.toSet() }

    // 顯示個人檔案 Dialog
    selectedFriend?.let { friend ->
        val isFriend = friendIdSet.contains(friend.id)
        FriendProfileDialog(
            friend = friend,
            alreadyRequested = sentRequests.contains(friend.id),
            isFriend = isFriend,
            onDismiss = { selectedFriend = null },
            onToggleFriendRequest = { viewModel.toggleFriendRequest(friend.id) }
        )
    }

    // 螢幕進入或 refresh 時載入好友資料
    LaunchedEffect(refreshKey) {
        Log.d("FriendScreen", "LaunchedEffect triggered with refreshKey = $refreshKey")
        viewModel.loadFriendData()
        viewModel.clearSearch()
        query = ""
    }

    // 顯示全部好友 bottom sheet
    if (showFriendList) {
        ModalBottomSheet(
            onDismissRequest = { showFriendList = false },
            sheetState = sheetState
        ) {
            Log.d("FriendScreen", "Current friendList size = ${friendList.size}")
            Text(
                text = "All Friends",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.8f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (friendList.isEmpty()) {
                    item {
                        Text(
                            "目前尚未加入任何好友",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(friendList) { friend ->
                        Log.d("FriendScreen", "Friend item: ${friend.username} (${friend.id})")
                        FriendListItem(
                            friend = friend,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { selectedFriend = friend }
                        )
                    }
                }
            }
        }
    }

    // 畫面主內容
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 🔍 搜尋欄
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { newQuery ->
                        if (newQuery.isNotBlank()) {
                            viewModel.onSearchQueryChanged(newQuery)
                            viewModel.searchUser(newQuery)
                        }
                    },
                    modifier = Modifier.weight(9f)
                )
                IconButton(
                    onClick = {
                        if (friendList.isEmpty()) {
                            viewModel.loadFriendData()  // ❗懶加載保險起見
                        }
                        showFriendList = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Show All Friends"
                    )
                }
            }
        }

        // ⏳ 等待回應列表
        if (pendingRequests.isNotEmpty()) {
            item {
                SectionHeader("Pending Requests", modifier = Modifier.padding(vertical = 8.dp))
            }
            items(pendingRequests) { request ->
                PendingFriendRequestCard(
                    avatarUrl = request.fromAvatarUrl,
                    username = request.fromUsername,
                    timestamp = formatRelativeTime(request.timestamp),
                    onAccept = { viewModel.respondToRequest(request.fromUserId, true) },
                    onReject = { viewModel.respondToRequest(request.fromUserId, false) }
                )
            }
        }

        // 🔍 搜尋結果區塊
        if (searchQuery.isNotBlank()) {
            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                searchResult?.let { result ->
                    item {
                        SectionHeader("Search Result", modifier = Modifier.padding(vertical = 8.dp))
                    }
                    item {
                        FriendListItem(
                            friend = result,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { selectedFriend = result }
                        )
                    }
                } ?: item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("找不到使用者", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}


@Composable
fun PendingFriendRequestCard(
    avatarUrl: String,
    username: String,
    timestamp: String,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl.ifBlank { "https://source.unsplash.com/64x64/?face" },
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
            placeholder = painterResource(id = R.drawable.user),
            error = painterResource(id = R.drawable.user),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = username,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = formatRelativeTime(timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Button(
                onClick = onAccept,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)) // 藍色 Confirm
            ) {
                Text("Confirm", color = Color.White)
            }

            OutlinedButton(
                onClick = onReject,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Delete")
            }
        }
    }
}

@Composable
fun FriendListItem(
    friend: UserSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .height(72.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = friend.avatarUrl?.takeIf { it.isNotBlank() }
                ?: "https://source.unsplash.com/280x160/?face",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp)),
            placeholder = painterResource(id = R.drawable.user),
            error = painterResource(id = R.drawable.user)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                friend.username,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

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
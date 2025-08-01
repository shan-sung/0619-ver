package com.example.myapplication.ui.screens.myplans.trip.details

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.viewmodel.myplans.TripDetailViewModel

@Composable
fun ShareTripDialog(
    tripId: String,
    memberIds: List<String>,
    onDismiss: () -> Unit
) {
    val viewModel: TripDetailViewModel = hiltViewModel()
    val context = LocalContext.current
    var triggerShare by remember { mutableStateOf(false) }
    var showFriendPicker by remember { mutableStateOf(false) }

    // 分享連結
    LaunchedEffect(triggerShare) {
        if (triggerShare) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, "加入我的旅程：點此開啟 ➜ https://yourapp.com/trips/$tripId")
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "分享行程"))
            triggerShare = false
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("分享行程") },
        text = {
            Column {
                Text("選擇一種方式分享行程")
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showFriendPicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("從好友列表中選擇")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { triggerShare = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("分享連結")
                }

                if (showFriendPicker) {
                    FriendPickerDialog(
                        onDismiss = { showFriendPicker = false },
                        onConfirm = { selectedFriendIds ->
                            viewModel.inviteFriends(tripId, selectedFriendIds) {
                                showFriendPicker = false
                                onDismiss()
                            }
                        },
                        existingMemberIds = memberIds
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
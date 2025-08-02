package com.example.myapplication.ui.screens.b_myplans.d_features

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.viewmodel.friend.FriendViewModel

@Composable
fun FriendPickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    existingMemberIds: List<String>,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val friends by viewModel.friendList.collectAsState()
    val selectedIds = remember {
        mutableStateListOf<String>().apply {
            addAll(existingMemberIds)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("邀請朋友加入行程") },
        text = {
            Column(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                friends.forEach { friend ->
                    val isAlreadyInTrip = existingMemberIds.contains(friend.id)
                    val isSelected = selectedIds.contains(friend.id)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (!isAlreadyInTrip) Modifier.clickable {
                                    if (isSelected) selectedIds.remove(friend.id)
                                    else selectedIds.add(friend.id)
                                } else Modifier
                            )
                            .padding(8.dp)
                    ) {
                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null,
                            enabled = !isAlreadyInTrip
                        )
                        Text(
                            text = friend.username + if (isAlreadyInTrip) "（已加入）" else "",
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (isAlreadyInTrip) Color.Gray else Color.Unspecified
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val newInvites = selectedIds.filterNot { existingMemberIds.contains(it) }
                onConfirm(newInvites)
            }) {
                Text("確定加入")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
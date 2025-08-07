package com.example.myapplication.ui.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.OpeningHoursSection
import com.example.myapplication.ui.screens.b_myplans.c_itinerary.crud.EditScheduleDialog
import com.example.myapplication.viewmodel.TripDetailViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleDetailBottomSheet(
    item: ScheduleItem,
    navController: NavController,
    travelId: String,
    index: Int,
    pagerState: PagerState, // ✅ 明確定義進來
    onClose: () -> Unit
) {
    val viewModel: TripDetailViewModel = hiltViewModel()
    var showEditDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val travel = uiState.data

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            when (event) {
                is TripDetailViewModel.TripUiEvent.ScheduleUpdated -> {
                    println("📍收到跳轉事件，前往 DayIndex=${event.dayIndex}")
                    pagerState.scrollToPage(event.dayIndex - 1)
                }
                is TripDetailViewModel.TripUiEvent.CloseDialogAndBottomSheet -> {
                    println("📍關閉 Dialog 與 BottomSheet")
                    showEditDialog = false
                    onClose()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
    ) {
        ScheduleHeader(
            item = item,
            travelId = travelId,
            index = index,
            viewModel = viewModel,
            onClose = onClose,
            onEdit = { showEditDialog = true }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScheduleBasicInfo(item)
            TransportationOptions()
            AISuggestion()
            BottomActionButtons(item)
        }

        // ✅ 安全判斷資料再顯示 Dialog
        if (showEditDialog && travel != null) {
            EditScheduleDialog(
                currentTrip = travel,
                scheduleItem = item,
                itemIndex = index,
                onDismiss = { showEditDialog = false },
                viewModel = viewModel
            )
        }
    }
}


@Composable
fun TransportOptionRow(title: String, subtitle: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun ScheduleHeader(
    item: ScheduleItem,
    travelId: String,
    index: Int,
    viewModel: TripDetailViewModel,
    onClose: () -> Unit,
    onEdit: () -> Unit // ✅ 改成 callback 而非 navigate
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("確認刪除") },
            text = { Text("你確定要刪除這個行程嗎？此操作無法復原。") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteConfirm = false
                    viewModel.deleteScheduleItemAndRefresh(
                        travelId = travelId,
                        day = item.day,
                        index = index,
                        onResult = { success ->
                            if (success) onClose()
                        }
                    )
                }) {
                    Text("刪除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            item.place.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )

        Box {
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More options")
            }

            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("編輯") },
                    onClick = {
                        menuExpanded = false
                        onEdit() // ✅ 觸發 dialog 彈出
                    }
                )
                DropdownMenuItem(
                    text = { Text("刪除") },
                    onClick = {
                        menuExpanded = false
                        showDeleteConfirm = true
                    }
                )
            }
        }
    }
}

@Composable
fun ScheduleBasicInfo(item: ScheduleItem) {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    Column {
        item.startTime?.let { start ->
            item.endTime?.let { end ->
                Text("預計時間：${start.format(formatter)} - ${end.format(formatter)}", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
            }
        }
        item.place.address?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
        }
        item.place.openingHours?.takeIf { it.isNotEmpty() }?.let {
            OpeningHoursSection(it)
        }
        Spacer(Modifier.height(12.dp))
    }
}

@Composable
fun TransportationOptions() {
    Column {
        Text("Transportation Options", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        listOf(
            Triple("Car", "Estimated time: 30 minutes", Icons.Default.DirectionsCar),
            Triple("Bus", "Estimated time: 45 minutes", Icons.Default.DirectionsBus),
            Triple("Bike", "Estimated time: 1 hour", Icons.AutoMirrored.Filled.DirectionsBike)
        ).forEach { (title, subtitle, icon) ->
            TransportOptionRow(title, subtitle, icon)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun AISuggestion() {
    Column {
        Text("AI Suggestion", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        TransportOptionRow(
            "Car (Recommended)",
            "Fastest route, minimal traffic",
            Icons.Default.DirectionsCar
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun BottomActionButtons(item: ScheduleItem) {
    val context = LocalContext.current
    Log.d("ScheduleDebug", "lat=${item.place.lat}, lng=${item.place.lng}, name=${item.place.name}")

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // ➤ 打開 Google Maps
        Button(
            onClick = {
                val lat = item.place.lat
                val lng = item.place.lng
                val name = item.place.name
                if (lat != null && lng != null) {
                    val uri = Uri.parse("geo:$lat,$lng?q=${Uri.encode(name)}")
                    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage("com.google.android.apps.maps")
                    }

                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        // 處理使用者未安裝 Google Maps 的情況
                        android.widget.Toast.makeText(context, "請先安裝 Google Maps", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("Open in Maps")
        }

        // 保留 Start 行程按鈕（未來你可實作打卡/導航）
        Button(
            onClick = {
                // TODO: 實作開始行程的功能（ex. 打卡 or 導航）
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("Start")
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
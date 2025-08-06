package com.example.myapplication.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.model.ScheduleItem
import com.example.myapplication.ui.components.dialogs.placedetaildialog.comp.OpeningHoursSection
import java.time.format.DateTimeFormatter

@Composable
fun ScheduleDetailBottomSheet(item: ScheduleItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.6f)
    ) {
        // Header + Menu（封裝在一起）
        ScheduleHeader(item = item)

        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 56.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ScheduleBasicInfo(item)
            TransportationOptions()
            AISuggestion()
            BottomActionButtons()
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
fun ScheduleHeader(item: ScheduleItem) {
    var menuExpanded by remember { mutableStateOf(false) }

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
                DropdownMenuItem(text = { Text("編輯") }, onClick = {
                    menuExpanded = false
                    // TODO
                })
                DropdownMenuItem(text = { Text("刪除") }, onClick = {
                    menuExpanded = false
                    // TODO
                })
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
fun BottomActionButtons() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { /* TODO: Open in Maps */ },
            modifier = Modifier.weight(1f)
        ) {
            Text("Open in Maps")
        }

        Button(
            onClick = { /* TODO: Start */ },
            modifier = Modifier.weight(1f)
        ) {
            Text("Start")
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}
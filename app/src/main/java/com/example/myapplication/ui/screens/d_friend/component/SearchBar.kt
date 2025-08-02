package com.example.myapplication.ui.screens.d_friend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.components.SearchBar

@Composable
fun SearchSection(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onShowFriendList: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            modifier = Modifier.weight(9f)
        )

        IconButton(
            onClick = onShowFriendList,
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

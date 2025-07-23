package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SearchBar(
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        label = { Text("搜尋使用者名稱或信箱") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            Row {
                if (query.isNotBlank()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "清除")
                    }
                }
                IconButton(onClick = {
                    if (query.isNotBlank()) {
                        onSearch(query.trim())
                    }
                }) {
                    Icon(Icons.Default.Search, contentDescription = "搜尋")
                }
            }
        },
        keyboardActions = KeyboardActions(
            onSearch = {
                if (query.isNotBlank()) {
                    onSearch(query.trim())
                }
            }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        )
    )
}

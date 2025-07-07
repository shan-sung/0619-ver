package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier.padding(16.dp),
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.headlineSmall
) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TopBarText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    style: TextStyle = MaterialTheme.typography.headlineSmall
) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun QuesText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    style: TextStyle = MaterialTheme.typography.titleLarge
) {
    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier,
        textAlign = TextAlign.Center
    )
}
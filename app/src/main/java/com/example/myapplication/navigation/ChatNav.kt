package com.example.myapplication.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.myapplication.ui.screens.ChatRoomScreen

fun NavGraphBuilder.chatNav() {
    composable("chat") {
        ChatRoomScreen()
    }
}

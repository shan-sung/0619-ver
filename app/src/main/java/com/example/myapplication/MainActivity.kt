package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myapplication.navigation.main.RootNavGraph
import com.example.myapplication.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // 啟用 DI 功能
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { // 使用 Jetpack Compose
            AppTheme { // 使用自定義的主題
                RootNavGraph() // 進入Root navigation
            }
        }
    }
}
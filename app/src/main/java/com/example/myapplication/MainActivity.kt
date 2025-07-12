package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.model.CurrentUser
import com.example.myapplication.ui.components.MainScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                RootNavGraph()
            }
        }
    }
}

@Composable
fun RootNavGraph() {
    val navController = rememberNavController()
    val startDestination = if (CurrentUser.isLoggedIn()) "main" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("main") {
            MainScreen() // ✅ MainScreen 自己有內部 navController 和 AppNavGraph
        }
    }
}


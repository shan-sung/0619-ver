package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.navigation.AppNavGraph
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.components.TopBar

@Composable
fun MainScreen(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showTopBar = currentRoute in listOf("trips", "saved", "profile", "create", "trip_detail/{id}")
    val showBottomBar = currentRoute in listOf("explore", "trips", "saved", "profile")


    Scaffold(
        topBar = { if (showTopBar) TopBar(title = currentRoute?.replaceFirstChar { it.uppercase() } ?: "")},
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

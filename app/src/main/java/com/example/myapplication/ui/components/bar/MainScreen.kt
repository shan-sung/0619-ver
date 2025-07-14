package com.example.myapplication.ui.components.bar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.main.AppNavGraph
import com.example.myapplication.navigation.routes.Routes

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showTopBar = currentRoute?.run {
        startsWith("trip_detail/") || this in listOf(
            Routes.MyPlans.MAIN,
            Routes.Saved.MAIN,
            Routes.Profile.MAIN,
            Routes.MyPlans.CREATE
        )
    } ?: false

    val showBottomBar = currentRoute in listOf(
        Routes.Explore.MAIN,
        Routes.MyPlans.MAIN,
        Routes.Saved.MAIN,
        Routes.Profile.MAIN
    )

    val topBarTitle = when {
        currentRoute?.startsWith("trip_detail/") == true -> "My Trip"
        currentRoute == Routes.MyPlans.MAIN -> "Trips"
        currentRoute == Routes.Saved.MAIN -> "Saved"
        currentRoute == Routes.Profile.MAIN -> "Profile"
        currentRoute == Routes.MyPlans.CREATE -> "Create"
        else -> currentRoute?.replaceFirstChar { it.uppercase() } ?: ""
    }

    Scaffold(
        topBar = { if (showTopBar) TopBar(title = topBarTitle) },
        bottomBar = { if (showBottomBar) BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

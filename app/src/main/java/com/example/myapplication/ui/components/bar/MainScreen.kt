package com.example.myapplication.ui.components.bar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.AppNavGraph
import com.example.myapplication.navigation.routes.Routes

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // 👇 新增：記錄當前 route 對應的 refresh key
    val refreshKeys = remember { mutableStateMapOf<String, Int>() }
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // 👇 根據 route 抓目前頁面的 refreshKey
    val refreshKey = currentRoute?.let { refreshKeys[it] } ?: 0

    val showTopBar = currentRoute?.run {
        startsWith("trip_detail/") || this in listOf(
            Routes.MyPlans.MAIN,
            Routes.Saved.MAIN,
            Routes.Profile.MAIN,
            Routes.MyPlans.CREATE,
            Routes.MyPlans.DETAIL,
            Routes.MyPlans.SELECT_FROM_SAVED
        )
    } ?: false

    val showBottomBar = currentRoute in listOf(
        Routes.Explore.MAIN,
        Routes.MyPlans.MAIN,
        Routes.Saved.MAIN,
        Routes.Friend.MAIN,
        Routes.Profile.MAIN
    )

    val topBarTitle = when {
        currentRoute == Routes.MyPlans.DETAIL -> "My Trip"
        currentRoute == Routes.MyPlans.MAIN -> "Trips"
        currentRoute == Routes.Saved.MAIN -> "Saved"
        currentRoute == Routes.Profile.MAIN -> "Profile"
        currentRoute == Routes.MyPlans.CREATE -> "Create"
        currentRoute == Routes.MyPlans.SELECT_FROM_SAVED -> "Select Attraction"
        else -> currentRoute?.replaceFirstChar { it.uppercase() } ?: ""
    }

    Scaffold(
        topBar = { if (showTopBar) TopBar(title = topBarTitle) },
        bottomBar = {
            if (showBottomBar) BottomNavigationBar(
                navController = navController,
                currentRoute = currentRoute,
                onReselect = {
                    currentRoute?.let {
                        // 👇 增加 refresh key 值（等於重整）
                        refreshKeys[it] = (refreshKeys[it] ?: 0) + 1
                    }
                }
            )
        }
    ) { innerPadding ->
        AppNavGraph(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding),
            refreshKey = refreshKey // 傳進去頁面組件中
        )
    }
}
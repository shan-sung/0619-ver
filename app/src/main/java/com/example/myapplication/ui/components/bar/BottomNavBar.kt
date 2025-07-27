package com.example.myapplication.ui.components.bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.myapplication.navigation.routes.Routes

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.App.EXPLORE, "Explore", Icons.Filled.Explore),
    BottomNavItem(Routes.App.MY_PLANS, "My Plans", Icons.Filled.Map),
    BottomNavItem(Routes.App.SAVED, "Saved", Icons.Filled.FavoriteBorder),
    BottomNavItem(Routes.App.FRIEND, "Friend", Icons.Filled.People),
    BottomNavItem(Routes.App.PROFILE, "Profile", Icons.Filled.Person)
)

@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    onReselect: () -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute == item.route) {
                        onReselect() // ✅ 點同一頁就重整
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}

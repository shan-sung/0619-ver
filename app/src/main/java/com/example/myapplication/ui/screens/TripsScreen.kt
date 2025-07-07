package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.TripNavHost
import com.example.myapplication.ui.components.AddFab
import com.example.myapplication.ui.screens.trips.TripTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val startDestination = TripTab.CREATED
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    // 新增：只給 Tab 用的內部 navController
    val tabNavController = rememberNavController()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            AddFab(onClick = { navController.navigate("create") }) // 外層 navController
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                TripTab.entries.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            tabNavController.navigate(tab.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        text = { Text(tab.label) }
                    )
                }
            }
            TripNavHost(navController = tabNavController, startDestination = startDestination)
        }
    }
}

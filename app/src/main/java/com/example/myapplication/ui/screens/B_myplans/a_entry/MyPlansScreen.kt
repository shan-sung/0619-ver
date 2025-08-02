package com.example.myapplication.ui.screens.b_myplans.a_entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.navigation.details.TripNavHost
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.AppFab
import com.example.myapplication.ui.screens.b_myplans._comp.TripTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripsScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val startDestination = TripTab.CREATED
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    // 給 Tab 專用的 NavController
    val tabNavController = rememberNavController()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            AppFab(onClick = { navController.navigate(Routes.MyPlans.CREATE) }, icon = Icons.Filled.Add, contentDescription = "Add") // 使用外層 navController
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            TripTabBar(selectedIndex = selectedTabIndex) { index ->
                selectedTabIndex = index
                val tab = TripTab.entries[index]
                tabNavController.navigate(tab.route) {
                    popUpTo(tabNavController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }

            TripNavHost(
                navController = tabNavController,
                startDestination = startDestination,
                parentNavController = navController,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripTabBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    PrimaryTabRow(selectedTabIndex = selectedIndex) {
        TripTab.entries.forEachIndexed { index, tab ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(tab.label) }
            )
        }
    }
}

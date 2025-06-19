package com.example.myapplication.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.Attraction
import com.example.myapplication.data.mockTravels

@Composable
fun ExploreSection(
    navController: NavController,
    isLoading: Boolean,
    attractions: List<Attraction>
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Featured")
            MoreIconButton {
                navController.navigate("featured/more")
            }
        }
        CardRowLib(navController = navController, travels = mockTravels)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Attractions")
            MoreIconButton {
                navController.navigate("attractions/more")
            }
        }
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            attractions.isEmpty() -> {
                Text("附近沒有景點", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
            }
            else -> {
                CardColLib(navController = navController, attractions = attractions)
            }
        }
    }
}
package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.ui.components.NextPre
import com.example.myapplication.ui.screens.creation.AgeRangeScreen
import com.example.myapplication.ui.screens.creation.BudgetScreen
import com.example.myapplication.ui.screens.creation.CitySelectionScreen
import com.example.myapplication.ui.screens.creation.DateRangeScreen
import com.example.myapplication.ui.screens.creation.PeopleCountScreen
import com.example.myapplication.ui.screens.creation.PreferencesScreen
import com.example.myapplication.ui.screens.creation.TitleInputScreen
import com.example.myapplication.ui.screens.creation.TransportOptionsScreen
import com.example.myapplication.viewmodel.TripCreationViewModel
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripWizardScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TripCreationViewModel = hiltViewModel()
) {
    val step by viewModel.step.collectAsState()

    val localDateSaver = Saver<LocalDate, String>(
        save = { it.toString() },
        restore = { LocalDate.parse(it) }
    )
    var title by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable(stateSaver = localDateSaver) { mutableStateOf(LocalDate.now()) }
    var endDate by rememberSaveable(stateSaver = localDateSaver) { mutableStateOf(LocalDate.now().plusDays(1)) }
    var peopleCount by rememberSaveable { mutableIntStateOf(1) }
    var ageRange by rememberSaveable { mutableStateOf("") }
    var prefer by rememberSaveable(
        stateSaver = listSaver(
            save = { it },
            restore = { it.toList() }
        )
    ) { mutableStateOf(listOf<String>()) }
    var transport by rememberSaveable(
        stateSaver = listSaver(
            save = { it },
            restore = { it.toList() }
        )
    ) { mutableStateOf(listOf<String>()) }
    var city by rememberSaveable(
        stateSaver = listSaver(
            save = { it },
            restore = { it.toList() }
        )
    ) { mutableStateOf(listOf<String>()) }
    var budget by rememberSaveable { mutableStateOf(10000) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 0) {
                    NextPre(text = "Previous",onClick = { viewModel.prevStep() })
                } else {
                    Spacer(modifier = Modifier.weight(1f)) // 保持對齊
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (step < 7) {
                    NextPre(text = "Next", onClick = {
                        when (step) {
                            0 -> viewModel.updateTitle(title)
                            1 -> {
                                viewModel.updateStartDate(startDate)
                                viewModel.updateEndDate(endDate)
                            }
                            2 -> viewModel.updatePeopleCount(peopleCount)
                            3 -> viewModel.updateAgeRange(ageRange)
                            4 -> viewModel.updatePreferences(prefer)
                            5 -> viewModel.updateTransportOptions(transport)
                            6 -> viewModel.updateCities(city)
                            7 -> viewModel.updateBudget(budget)
                        }
                        viewModel.nextStep()
                    })
                }
                else {
                    NextPre(text = "Submit", onClick = {
                        viewModel.updateBudget(budget)
                        viewModel.submitTrip()
                        navController.navigate("trips")
                    })
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp)) {
            when (step) {
                0 -> TitleInputScreen(title = title, onTitleChange = { title = it })
                1 -> DateRangeScreen(
                    startDate = startDate,
                    endDate = endDate,
                    onDateRangeChange = { start, end ->
                        startDate = start ?: LocalDate.now()
                        endDate = end ?: LocalDate.now().plusDays(1)
                    }
                )

                2 -> PeopleCountScreen(count = peopleCount, onCountChange = { peopleCount = it })
                3 -> AgeRangeScreen(value = ageRange, onValueChange = { ageRange = it })
                4 -> PreferencesScreen(selected = prefer, onChange = { prefer = it })
                5 -> TransportOptionsScreen(selected = transport, onChange = { transport = it })
                6 -> CitySelectionScreen(selected = city, onChange = { city = it })
                7 -> BudgetScreen(initialBudget = budget, onChange = { budget = it })
            }
        }
    }
}
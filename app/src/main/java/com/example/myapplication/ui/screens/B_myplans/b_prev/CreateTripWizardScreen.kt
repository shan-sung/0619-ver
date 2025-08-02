package com.example.myapplication.ui.screens.b_myplans.b_prev

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.myapplication.navigation.routes.Routes
import com.example.myapplication.ui.components.AppExtendedFab
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.AgeRangeScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.BudgetScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.CitySelectionScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.DailyHourRangeScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.DateRangeScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.GoogleRatingScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.PeopleCountScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.PreferencesScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.TitleInputScreen
import com.example.myapplication.ui.screens.b_myplans.b_prev.survey.TransportOptionsScreen
import com.example.myapplication.viewmodel.myplans.TripCreationViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripWizardScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: TripCreationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val step by viewModel.step.collectAsState()

    val localDateSaver = Saver<LocalDate, String>(
        save = { it.toString() },
        restore = { LocalDate.parse(it) }
    )

    val stringListSaver = listSaver<List<String>, String>(
        save = { it },
        restore = { it.toList() }
    )

    var title by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable(stateSaver = localDateSaver) { mutableStateOf(LocalDate.now()) }
    var endDate by rememberSaveable(stateSaver = localDateSaver) { mutableStateOf(LocalDate.now().plusDays(1)) }
    var peopleCount by rememberSaveable { mutableIntStateOf(1) }
    var startHour by rememberSaveable { mutableIntStateOf(9) }
    var endHour by rememberSaveable { mutableIntStateOf(20) }
    var ageRange by rememberSaveable { mutableStateOf("") }
    var prefer by rememberSaveable(stateSaver = stringListSaver) { mutableStateOf(listOf()) }
    var transport by rememberSaveable(stateSaver = stringListSaver) { mutableStateOf(listOf()) }
    var city by rememberSaveable(stateSaver = stringListSaver) { mutableStateOf(listOf()) }
    var budget by rememberSaveable { mutableIntStateOf(10000) }
    var google by rememberSaveable { mutableStateOf(false) }

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
                    AppExtendedFab(text = "上一步", onClick = { viewModel.prevStep() })
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (step < 9) {
                    AppExtendedFab(text = "下一步", onClick = {
                        val isValid = when (step) {
                            0 -> title.isNotBlank()
                            1 -> !endDate.isBefore(startDate)
                            2 -> peopleCount > 0
                            3 -> ageRange.isNotBlank()
                            4 -> prefer.isNotEmpty()
                            5 -> transport.isNotEmpty()
                            6 -> city.isNotEmpty()
                            7 -> budget > 0
                            8 -> true
                            9 -> endHour > startHour
                            else -> true
                        }

                        if (!isValid) {
                            Toast.makeText(context, "請完整填寫本頁資訊", Toast.LENGTH_SHORT).show()
                            return@AppExtendedFab
                        }

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
                            8 -> viewModel.updateGoogle(google)
                            9 -> viewModel.updateDailyHourRange(startHour, endHour)
                        }
                        viewModel.nextStep()
                    })
                } else {
                    AppExtendedFab(text = "Submit", onClick = {
                        val allValid = title.isNotBlank() &&
                                !endDate.isBefore(startDate) &&
                                peopleCount > 0 &&
                                ageRange.isNotBlank() &&
                                prefer.isNotEmpty() &&
                                transport.isNotEmpty() &&
                                city.isNotEmpty() &&
                                budget > 0 &&
                                startHour < endHour

                        if (!allValid) {
                            Toast.makeText(context, "請確認所有欄位皆已填寫", Toast.LENGTH_SHORT).show()
                            return@AppExtendedFab
                        }

                        viewModel.updateAllInputs(
                            title = title,
                            startDate = startDate,
                            endDate = endDate,
                            peopleCount = peopleCount,
                            ageRange = ageRange,
                            preferences = prefer,
                            transport = transport,
                            cities = city,
                            budget = budget,
                            google = google,
                            dailyStartHour = startHour,
                            dailyEndHour = endHour
                        )
                        viewModel.submitTrip { travel ->
                            navController.currentBackStackEntry?.savedStateHandle?.set("travel", travel)
                            navController.navigate(Routes.MyPlans.PREVIEW)
                        }
                    })
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LinearProgressIndicator(
                progress = { (step + 1) / 10f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp)
            )

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
                8 -> GoogleRatingScreen(value = google, onValueChange = { google = it })
                9 -> DailyHourRangeScreen(
                    startHour = startHour,
                    endHour = endHour,
                    onChange = { s, e ->
                        startHour = s
                        endHour = e
                    }
                )
            }
        }
    }
}
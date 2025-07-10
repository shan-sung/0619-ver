package com.example.myapplication.ui.screens.trips

enum class TripTab(val label: String, val route: String) {
    CREATED("Created", "created_trips"),
    PARTICIPATING("Participating", "participating_trips");

    companion object {
        fun fromRoute(route: String?): TripTab =
            entries.firstOrNull { it.route == route } ?: CREATED
    }
}

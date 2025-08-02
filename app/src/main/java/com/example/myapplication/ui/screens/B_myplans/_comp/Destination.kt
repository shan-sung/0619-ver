package com.example.myapplication.ui.screens.b_myplans._comp

import com.example.myapplication.navigation.routes.Routes

enum class TripTab(val label: String, val route: String) {
    CREATED("Created", Routes.MyPlans.Tab.CREATED),
    PARTICIPATING("Participating", Routes.MyPlans.Tab.PARTICIPATING);
}

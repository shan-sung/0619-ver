package com.example.myapplication.ui.components.picker

import android.app.DatePickerDialog
import android.content.Context
import java.time.LocalDate

fun showDatePicker(
    context: Context,
    startDate: LocalDate,
    endDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val now = LocalDate.now()
    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected(LocalDate.of(year, month + 1, dayOfMonth))
        },
        now.year, now.monthValue - 1, now.dayOfMonth
    )
    dialog.datePicker.minDate = startDate.toEpochDay() * 86400000
    dialog.datePicker.maxDate = endDate.toEpochDay() * 86400000
    dialog.show()
}
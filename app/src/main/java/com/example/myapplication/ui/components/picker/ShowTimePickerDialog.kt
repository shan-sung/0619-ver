package com.example.myapplication.ui.components.picker

import android.app.TimePickerDialog
import android.content.Context
import java.time.LocalTime

fun showTimePickerDialog(
    context: Context,
    initialTime: LocalTime?,
    onTimeSelected: (LocalTime) -> Unit
) {
    val now = initialTime ?: LocalTime.of(9, 0)
    TimePickerDialog(
        context,
        { _, hour, minute -> onTimeSelected(LocalTime.of(hour, minute)) },
        now.hour, now.minute, true
    ).show()
}
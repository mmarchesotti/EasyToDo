package com.mmarchesotti.easytodo.ui.components

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatTime(time: LocalTime?): String {
    if (time == null) return ""

    val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
    return time.format(formatter)
}

fun formatDate(date: LocalDate?): String {
    if (date == null) return ""

    val formatter = DateTimeFormatter.ofPattern("d MMM, yyyy", Locale.getDefault())
    return date.format(formatter)
}
package com.mmarchesotti.easytodo.ui.components

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun combineDateAndTime(dateMillis: Long?, hour: Int, minute: Int): Long? {
    if (dateMillis == null) return null
    val calendar = Calendar.getInstance().apply {
        timeInMillis = dateMillis // Set to the selected date (at midnight UTC from DatePicker)
        // Adjust for local timezone if DatePicker's selectedDateMillis is UTC midnight
        // For simplicity, assuming selectedDateMillis is already in local timezone midnight
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a, MMM dd", Locale.getDefault()) // Example format: 02:30 PM, May 31
    return sdf.format(Date(timestamp))
}
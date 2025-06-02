package com.mmarchesotti.easytodo.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit, // This lambda gives you the hour and minute
    title: String = "Select Time",
    initialHour: Int = LocalTime.now().hour, // Default to current hour
    initialMinute: Int = LocalTime.now().minute, // Default to current minute
    is24Hour: Boolean = true
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            TimePicker(state = timePickerState) // The actual clock UI
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute) // Pass selected time out
                    onDismissRequest() // Dismiss the dialog
                }
            ) { Text("OK") }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) { Text("Cancel") }
        }
    )
}
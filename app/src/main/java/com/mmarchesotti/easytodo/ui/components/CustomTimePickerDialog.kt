package com.mmarchesotti.easytodo.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog( // You can name it whatever you like, e.g., MyTimePickerDialog
    onDismissRequest: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    title: String = "Select Time", // Optional title parameter
    is24Hour: Boolean = true // Optional: set true for 24-hour format
) {
    val timePickerState = rememberTimePickerState(is24Hour = is24Hour)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            // You might want to wrap TimePicker in a Box with alignment for better presentation
            // e.g., Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { ... }
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                    onDismissRequest() // Usually dismiss after confirm
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
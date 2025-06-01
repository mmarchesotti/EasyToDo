package com.mmarchesotti.easytodo.ui.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddTaskDialog(
    taskName: String,
    onTaskNameChange: (String) -> Unit,
    selectedStartDateText: String,
    onSelectStartDate: () -> Unit, // Callback to show start date picker
    selectedEndDateText: String,
    onSelectEndDate: () -> Unit,   // Callback to show end date picker
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Task") },
        text = {
            Column {
                OutlinedTextField(
                    value = taskName,
                    onValueChange = onTaskNameChange,
                    label = { Text("Task Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Start Time:")
                Button(onClick = onSelectStartDate) { // Button to trigger date/time selection
                    Text(selectedStartDateText)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("End Time:")
                Button(onClick = onSelectEndDate) {   // Button to trigger date/time selection
                    Text(selectedEndDateText)
                }
            }
        },
        confirmButton = { Button(onClick = onSave) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
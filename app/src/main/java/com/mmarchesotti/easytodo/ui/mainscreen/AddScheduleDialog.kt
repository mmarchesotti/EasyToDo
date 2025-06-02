package com.mmarchesotti.easytodo.ui.mainscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleDialog(
    scheduleName: String,
    onScheduleNameChange: (String) -> Unit,
    selectedDate: LocalDate?,
    onSelectDateClicked: () -> Unit,
    selectedStartTime: LocalTime?,
    onSelectStartTimeClicked: () -> Unit,
    selectedEndTime: LocalTime?,
    onSelectEndTimeClicked: () -> Unit,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Schedule") },
        text = {
            Column {
                OutlinedTextField(
                    value = scheduleName,
                    onValueChange = onScheduleNameChange,
                    label = { Text("Schedule Name") },
                    singleLine = true,
                    isError = errorMessage?.contains("name", ignoreCase = true) == true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Date Selection ---
                Text("Date:")
                Text(
                    text = selectedDate?.format(dateFormatter) ?: "Select Date",
                    style = MaterialTheme.typography.bodyLarge, // Make it look like normal text
                    color = if (selectedDate == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface, // Hint if not selected
                    modifier = Modifier
                        .fillMaxWidth() // Make the whole row clickable
                        .clickable(onClick = onSelectDateClicked)
                        .padding(vertical = 8.dp) // Add padding for better touch target and appearance
                    // textDecoration = TextDecoration.Underline // Optional: to make it look more like a link
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- Start Time Selection ---
                Text("Start Time:")
                Text(
                    text = selectedStartTime?.format(timeFormatter) ?: "Select Start Time",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedStartTime == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSelectStartTimeClicked)
                        .padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // --- End Time Selection ---
                Text("End Time:")
                Text(
                    text = selectedEndTime?.format(timeFormatter) ?: "Select End Time",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedEndTime == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSelectEndTimeClicked)
                        .padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = { Button(onClick = onSave) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
package com.mmarchesotti.easytodo.ui.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmarchesotti.easytodo.ui.components.formatDate
import com.mmarchesotti.easytodo.ui.components.formatTime
import java.time.LocalDate
import java.time.LocalTime

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

                Text("Date:")
                Button(onClick = onSelectDateClicked) {
                    Text(formatDate(selectedDate))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("Start Time:")
                Button(onClick = onSelectStartTimeClicked) {
                    Text(formatTime(selectedStartTime))
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text("End Time:")
                Button(onClick = onSelectEndTimeClicked) {
                    Text(formatTime(selectedEndTime))
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = { Button(onClick = onSave) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
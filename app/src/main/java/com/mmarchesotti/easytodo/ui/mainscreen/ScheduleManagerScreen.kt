package com.mmarchesotti.easytodo.ui.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mmarchesotti.easytodo.ui.components.CustomTimePickerDialog
import com.mmarchesotti.easytodo.viewmodel.ScheduleViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleManagerScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = viewModel(),
    onNavigateToDailySchedules: () -> Unit
) {
    val currentSchedule by viewModel.currentSchedule.collectAsStateWithLifecycle()
    val nextSchedule by viewModel.nextSchedule.collectAsStateWithLifecycle()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var scheduleNameInput by rememberSaveable { mutableStateOf("") }

    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showStartTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showEndTimePickerDialog by rememberSaveable { mutableStateOf(false) }

    var selectedDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var selectedStartTime by rememberSaveable { mutableStateOf<LocalTime?>(null) }
    var selectedEndTime by rememberSaveable { mutableStateOf<LocalTime?>(null) }

    var dialogErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scheduleNameInput = ""
                selectedDate = LocalDate.now(ZoneId.systemDefault())
                selectedStartTime = null
                selectedEndTime = null
                dialogErrorMessage = null
                showDialog = true
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Schedule")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            MainScheduleCard(schedule = currentSchedule)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Upcoming", style = MaterialTheme.typography.bodyMedium)
            ScheduleCard(schedule = nextSchedule)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onNavigateToDailySchedules) { // Call the lambda to navigate
                Text("More")
            }
        }
    }

    // --- Add Schedule Dialog (Main Dialog) ---
    if (showDialog) {
        AddScheduleDialog(
            scheduleName = scheduleNameInput,
            onScheduleNameChange = {
                scheduleNameInput = it
                dialogErrorMessage = null // Clear error message when user types
            },
            selectedDate = selectedDate, // Pass LocalDate?
            onSelectDateClicked = {
                dialogErrorMessage = null // Clear error before showing picker
                showDatePickerDialog = true
            },
            selectedStartTime = selectedStartTime, // Pass LocalTime?
            onSelectStartTimeClicked = {
                dialogErrorMessage = null // Clear error
                showStartTimePickerDialog = true
            },
            selectedEndTime = selectedEndTime, // Pass LocalTime?
            onSelectEndTimeClicked = {
                dialogErrorMessage = null // Clear error
                showEndTimePickerDialog = true
            },
            errorMessage = dialogErrorMessage, // Pass the error message state
            onDismiss = {
                showDialog = false
                dialogErrorMessage = null // Clear error on dismiss
            },
            onSave = {
                // --- Validation ---
                if (scheduleNameInput.isBlank()) {
                    dialogErrorMessage = "Schedule name cannot be empty."
                    return@AddScheduleDialog // Exit onSave
                }
                if (selectedDate == null) {
                    dialogErrorMessage = "Please select a date."
                    return@AddScheduleDialog
                }
                if (selectedStartTime == null) {
                    dialogErrorMessage = "Please select a start time."
                    return@AddScheduleDialog
                }
                if (selectedEndTime == null) {
                    dialogErrorMessage = "Please select an end time."
                    return@AddScheduleDialog
                }

                // Now we know selectedDate, selectedStartTime, and selectedEndTime are not null
                // The user's original code had selectedStartTime!! > selectedEndTime!! which is for start after end.
                // For end time must be after start time, the condition is:
                if (selectedEndTime!!.isBefore(selectedStartTime!!) || selectedEndTime == selectedStartTime) {
                    dialogErrorMessage = "End time must be after start time."
                    return@AddScheduleDialog
                }

                // If all validations pass:
                viewModel.addSchedule( // Assuming viewModel.addSchedule takes these types
                    scheduleNameInput,
                    selectedDate!!,     // Safe non-null assertion due to checks above
                    selectedStartTime!!, // Safe non-null assertion
                    selectedEndTime!!    // Safe non-null assertion
                )
                showDialog = false // Close dialog
                // No need to clear dialogErrorMessage here, it's cleared when dialog opens/dismisses
            }
        )
    }

    // --- Start Date Picker Dialog ---
    if (showDatePickerDialog) {
        val initialDatePickerDate = selectedDate ?: LocalDate.now()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDatePickerDate
                .atStartOfDay(ZoneId.systemDefault()) // Convert LocalDate to millis for DatePickerState
                .toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Convert the selected millis back to LocalDate and update your state
                        selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePickerDialog = false // Dismiss date picker
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePickerDialog = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- Start Time Picker Dialog ---
    if (showStartTimePickerDialog) {
        val now = LocalTime.now() // Get current time to set as initial for picker
        CustomTimePickerDialog(
            initialHour = selectedStartTime?.hour ?: now.hour,
            initialMinute = selectedStartTime?.minute ?: now.minute,
            onDismissRequest = {
                showStartTimePickerDialog = false
            },
            onConfirm = { hour, minute ->
                selectedStartTime = LocalTime.of(hour, minute)
            }
        )
    }

    // --- End Time Picker Dialog ---
    if (showEndTimePickerDialog) {
        val now = LocalTime.now() // Get current time to set as initial for picker
        CustomTimePickerDialog(
            initialHour = selectedEndTime?.hour ?: now.hour,
            initialMinute = selectedEndTime?.minute ?: now.minute,
            onDismissRequest = {
                showEndTimePickerDialog = false
            },
            onConfirm = { hour, minute ->
                selectedEndTime = LocalTime.of(hour, minute)
            }
        )
    }
}
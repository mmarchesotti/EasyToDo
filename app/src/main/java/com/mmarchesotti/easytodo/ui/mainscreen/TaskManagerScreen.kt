package com.mmarchesotti.easytodo.ui.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.mmarchesotti.easytodo.ui.components.combineDateAndTime
import com.mmarchesotti.easytodo.ui.components.formatDateTime
import com.mmarchesotti.easytodo.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskManagerScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = viewModel(),
    onNavigateToDailyTasks: () -> Unit
) {
    val currentTask by viewModel.currentTask.collectAsStateWithLifecycle()
    val nextTask by viewModel.nextTask.collectAsStateWithLifecycle()

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var taskNameInput by rememberSaveable { mutableStateOf("") }

    var showStartDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showStartTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var tempSelectedStartDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var finalSelectedStartDateTimeMillis by rememberSaveable { mutableStateOf<Long?>(null) }

    var showEndDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showEndTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var tempSelectedEndDateMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var finalSelectedEndDateTimeMillis by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = { /* ... Your FAB ... */ }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Button to navigate to Daily Tasks View
            Button(onClick = onNavigateToDailyTasks) { // Call the lambda to navigate
                Text("View Tasks by Day")
            }
            Spacer(modifier = Modifier.height(16.dp))

            CurrentTaskView(task = currentTask)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Next", style = MaterialTheme.typography.headlineSmall)
            FollowingTaskView(task = nextTask)
        }
    }

    // --- Add Task Dialog (Main Dialog) ---
    if (showDialog) {
        AddTaskDialog(
            taskName = taskNameInput,
            onTaskNameChange = { taskNameInput = it },
            selectedStartDateText = finalSelectedStartDateTimeMillis?.let { formatDateTime(it) }
                ?: "Select Start Date/Time",
            onSelectStartDate = { showStartDatePickerDialog = true }, // This triggers the Start Date Picker
            selectedEndDateText = finalSelectedEndDateTimeMillis?.let { formatDateTime(it) }
                ?: "Select End Date/Time",
            onSelectEndDate = { showEndDatePickerDialog = true },   // This triggers the End Date Picker
            onDismiss = { showDialog = false },
            onSave = {
                if (taskNameInput.isNotBlank() && finalSelectedStartDateTimeMillis != null && finalSelectedEndDateTimeMillis != null) {
                    if (finalSelectedEndDateTimeMillis!! > finalSelectedStartDateTimeMillis!!) {
                        viewModel.addTask(
                            taskNameInput,
                            finalSelectedStartDateTimeMillis!!,
                            finalSelectedEndDateTimeMillis!!
                        )
                        showDialog = false
                    } else {
                        // TODO: Show error: end time must be after start time (e.g., using a Snackbar or a Text in the dialog)
                    }
                } else {
                    // TODO: Show error: all fields required
                }
            }
        )
    }

    // --- Start Date Picker Dialog ---
    if (showStartDatePickerDialog) {
        val datePickerState = rememberDatePickerState() // Manages the date picker's internal state
        DatePickerDialog(
            onDismissRequest = { showStartDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showStartDatePickerDialog = false // Dismiss date picker
                    tempSelectedStartDateMillis = datePickerState.selectedDateMillis // Store selected date (at midnight UTC)
                    if (tempSelectedStartDateMillis != null) {
                        showStartTimePickerDialog = true // <<<< NOW SHOW THE TIME PICKER
                    }
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePickerDialog = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- Start Time Picker Dialog ---
    if (showStartTimePickerDialog) {
        CustomTimePickerDialog(
            title = "Select Start Time",
            onDismissRequest = { showStartTimePickerDialog = false },
            onConfirm = { hour, minute ->
                // CustomTimePickerDialog calls onDismissRequest internally on confirm
                finalSelectedStartDateTimeMillis = combineDateAndTime(
                    tempSelectedStartDateMillis, // Use the date picked from DatePickerDialog
                    hour,
                    minute
                )
            }
        )
    }

    // --- End Date Picker Dialog ---
    if (showEndDatePickerDialog) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showEndDatePickerDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showEndDatePickerDialog = false
                    tempSelectedEndDateMillis = datePickerState.selectedDateMillis
                    if (tempSelectedEndDateMillis != null) {
                        showEndTimePickerDialog = true // <<<< NOW SHOW THE END TIME PICKER
                    }
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showEndDatePickerDialog = false }) { Text("Cancel") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- End Time Picker Dialog ---
    if (showEndTimePickerDialog) {
        CustomTimePickerDialog(
            title = "Select End Time",
            onDismissRequest = { showEndTimePickerDialog = false },
            onConfirm = { hour, minute ->
                finalSelectedEndDateTimeMillis = combineDateAndTime(
                    tempSelectedEndDateMillis, // Use the date picked from its DatePickerDialog
                    hour,
                    minute
                )
            }
        )
    }
}
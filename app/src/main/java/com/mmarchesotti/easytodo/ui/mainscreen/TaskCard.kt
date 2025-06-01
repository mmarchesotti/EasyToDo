package com.mmarchesotti.easytodo.ui.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmarchesotti.easytodo.data.Task
import com.mmarchesotti.easytodo.ui.components.formatTime

@Composable
fun TaskCard(task: Task?, modifier: Modifier = Modifier) { // Make task nullable if it can be
    if (task == null) {
        // Optionally, display something else or nothing if the task is null
        // For a list, you usually wouldn't pass a null task to a card item.
        Text("No task data provided.")
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth() // Make the card take the full width available
            .padding(vertical = 4.dp, horizontal = 8.dp), // Some margin around the card
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Add a slight shadow
        shape = MaterialTheme.shapes.medium, // Use rounded corners from the theme
        // colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Optional: customize card color
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Padding inside the card for its content
        ) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium // Or headlineSmall, titleLarge etc.
            )
            Spacer(modifier = Modifier.height(8.dp)) // Spacer between elements
            Text(
                text = "Start: ${formatTime(task.startTime)}", // Assuming formatTime is accessible
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Finish: ${formatTime(task.endTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
            // You could add more details here, like duration, status, etc.
        }
    }
}

@Composable
fun CurrentTaskView(task: Task?, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        if (task != null) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Start: ${formatTime(task.startTime)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Finish: ${formatTime(task.endTime)}",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = "No task to display.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun FollowingTaskView(task: Task?, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        if (task != null) {
            Text(
                text = task.name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Start: ${formatTime(task.startTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Finish: ${formatTime(task.endTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Text(
                text = "No task to display.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
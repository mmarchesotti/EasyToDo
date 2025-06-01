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
import com.mmarchesotti.easytodo.data.Schedule
import com.mmarchesotti.easytodo.ui.components.formatTime

@Composable
fun ScheduleCard(schedule: Schedule?, modifier: Modifier = Modifier) {
    if (schedule == null) {
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = schedule.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatTime(schedule.startTime)} - ${formatTime(schedule.endTime)}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MainScheduleCard(schedule: Schedule?, modifier: Modifier = Modifier) {
    if (schedule == null) {
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = schedule.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatTime(schedule.startTime)} - ${formatTime(schedule.endTime)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
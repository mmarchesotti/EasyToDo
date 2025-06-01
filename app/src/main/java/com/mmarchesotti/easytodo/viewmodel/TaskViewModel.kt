package com.mmarchesotti.easytodo.viewmodel // Or your specific package

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mmarchesotti.easytodo.data.AppDatabase
import com.mmarchesotti.easytodo.data.Task
import com.mmarchesotti.easytodo.data.TaskRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    // Internal mutable state for all tasks from the database
    private val _dbTasks = MutableStateFlow<List<Task>>(emptyList())

    // Publicly exposed StateFlow for the current task
    private val _currentTask = MutableStateFlow<Task?>(null)
    val currentTask: StateFlow<Task?> = _currentTask.asStateFlow()

    // Publicly exposed StateFlow for the next task
    private val _nextTask = MutableStateFlow<Task?>(null)
    val nextTask: StateFlow<Task?> = _nextTask.asStateFlow()

    // Optional: If you want to expose all tasks to the UI for other purposes
    // val allTasks: StateFlow<List<Task>> = _dbTasks.asStateFlow()
    private val _selectedDateForDayView = MutableStateFlow<LocalDate>(LocalDate.now()) // Default to today
    val selectedDateForDayView: StateFlow<LocalDate> = _selectedDateForDayView.asStateFlow()

    // StateFlow for tasks on the selected day. This will reactively update
    // when _selectedDateForDayView changes.
    @OptIn(ExperimentalCoroutinesApi::class)
    val tasksForSelectedDay: StateFlow<List<Task>> = _selectedDateForDayView.flatMapLatest { date ->
        val zoneId = ZoneId.systemDefault()
        val startOfDayMillis = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDayMillis = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        repository.getTasksStartingOnDay(startOfDayMillis, endOfDayMillis)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Keep active for 5s after last observer
        initialValue = emptyList()
    )

    init {
        val taskDao = AppDatabase.getInstance(application).taskDao()
        repository = TaskRepository(taskDao)

        viewModelScope.launch {
            // Check if the database is empty and insert sample tasks if it is.
            // .first() is a terminal operator for Flow, gets the first emission.
            if (repository.allTasks.first().isEmpty()) {
                insertSampleTasks() // Call your suspend fun to insert samples
            }

            // Then, start collecting ongoing updates.
            repository.allTasks.collectLatest { tasks ->
                _dbTasks.value = tasks
                updateCurrentAndNextTasks(tasks)
            }
        }

        viewModelScope.launch {
            while (true) {
                delay(10000L)
                updateCurrentAndNextTasks(_dbTasks.value)
            }
        }
    }

    // Ensure insertSampleTasks is a suspend function
    private suspend fun insertSampleTasks() { // Changed from public for internal use
        val now = System.currentTimeMillis()
        val oneHour = 3600000L
        val oneMinute = 60000L
        val sampleTasks = listOf(
            Task(name = "Breakfast (DB)", startTime = now - (3 * oneHour), endTime = now - (2*oneHour) - (30*oneMinute)),
            Task(name = "Morning Sync (DB)", startTime = now - (15 * oneMinute), endTime = now + (45 * oneMinute)), // Should be current
            Task(name = "Code Review (DB)", startTime = now + oneHour, endTime = now + (2 * oneHour)),
            Task(name = "Project Planning (DB)", startTime = now + (3 * oneHour), endTime = now + (4 * oneHour))
        ).sortedBy { it.startTime } // Good to sort before insert if order matters for other non-Flow queries
        sampleTasks.forEach { repository.insert(it) }
    }

    private fun updateCurrentAndNextTasks(tasks: List<Task>) {
        val currentTime = System.currentTimeMillis()
        val (foundCurrent, foundNext) = findCurrentAndNextTasksLogic(tasks, currentTime)
        _currentTask.value = foundCurrent
        _nextTask.value = foundNext
    }

    // Logic to find current and next tasks (moved from UI)
    private fun findCurrentAndNextTasksLogic(tasks: List<Task>, currentTime: Long): Pair<Task?, Task?> {
        var currentTaskResult: Task? = null
        var nextTaskResult: Task? = null

        // Tasks should be sorted by startTime, which repository.allTasks already provides
        currentTaskResult = tasks.find { task ->
            currentTime >= task.startTime && currentTime < task.endTime
        }

        if (currentTaskResult != null) {
            nextTaskResult = tasks.find { task ->
                task.startTime >= currentTaskResult.endTime && task.id != currentTaskResult.id
            }
        } else {
            nextTaskResult = tasks.find { task ->
                task.startTime > currentTime
            }
        }
        return Pair(currentTaskResult, nextTaskResult)
    }

    fun selectDateForDayView(date: LocalDate) {
        _selectedDateForDayView.value = date
    }

    // --- Methods for Task Operations (to be called by UI later) ---
    fun addTask(name: String, startTime: Long, endTime: Long) {
        viewModelScope.launch {
            val newTask = Task(name = name, startTime = startTime, endTime = endTime)
            repository.insert(newTask)
        }
    }

    // fun updateTask(task: Task) { viewModelScope.launch { repository.update(task) } }
    // fun deleteTask(task: Task) { viewModelScope.launch { repository.delete(task) } }
}
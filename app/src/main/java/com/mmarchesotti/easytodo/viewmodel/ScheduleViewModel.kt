package com.mmarchesotti.easytodo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mmarchesotti.easytodo.data.AppDatabase
import com.mmarchesotti.easytodo.data.Schedule
import com.mmarchesotti.easytodo.data.ScheduleRepository
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ScheduleRepository

    // Internal state to hold only schedules for the CURRENT DAY
    private val _schedulesForToday = MutableStateFlow<List<Schedule>>(emptyList())

    // Publicly exposed StateFlow for the current schedule (for today)
    private val _currentSchedule = MutableStateFlow<Schedule?>(null)
    val currentSchedule: StateFlow<Schedule?> = _currentSchedule.asStateFlow()

    // Publicly exposed StateFlow for the next schedule (for today)
    private val _nextSchedule = MutableStateFlow<Schedule?>(null)
    val nextSchedule: StateFlow<Schedule?> = _nextSchedule.asStateFlow()

    // For the "DailyTasksScreen" - allows viewing schedules for any selected date
    private val _selectedDateForDayView = MutableStateFlow<LocalDate>(LocalDate.now(ZoneId.systemDefault()))
    val selectedDateForDayView: StateFlow<LocalDate> = _selectedDateForDayView.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val schedulesForSelectedDay: StateFlow<List<Schedule>> = _selectedDateForDayView.flatMapLatest { date ->
        repository.getScheduleByDate(date) // Uses the new repository method
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // A simple flag to ensure sample data is inserted only once per ViewModel instance if needed
    private var sampleDataInsertedThisSession = false

    init {
        val scheduleDao = AppDatabase.getInstance(application).scheduleDao()
        repository = ScheduleRepository(scheduleDao)

        val today = LocalDate.now(ZoneId.systemDefault())

        viewModelScope.launch {
            // Initial check and potential insertion of sample data for today
            // This is a simplified check; a robust "is database empty" check would be better.
            val initialTodaySchedules = repository.getScheduleByDate(today).first()
            if (initialTodaySchedules.isEmpty() && !sampleDataInsertedThisSession) {
                insertSampleSchedulesForToday(today)
                sampleDataInsertedThisSession = true
            }

            // Continuously collect schedules for TODAY to update current/next
            repository.getScheduleByDate(today).collectLatest { todaySchedules ->
                _schedulesForToday.value = todaySchedules
                updateCurrentAndNextSchedules(todaySchedules)
            }
        }

        // Periodic refresh to update current/next schedule status against current time
        viewModelScope.launch {
            while (true) {
                updateCurrentAndNextSchedules(_schedulesForToday.value) // Use latest list of today's schedules
                delay(10000L) // Refresh every 10 seconds (adjust as needed)
            }
        }
    }

    private fun updateCurrentAndNextSchedules(schedulesForToday: List<Schedule>) {
        val now = LocalDateTime.now(ZoneId.systemDefault())
        val (foundCurrent, foundNext) = findCurrentAndNextSchedulesLogic(schedulesForToday, now)
        _currentSchedule.value = foundCurrent
        _nextSchedule.value = foundNext
    }

    // Updated logic to work with Schedule having LocalDate and LocalTime
    private fun findCurrentAndNextSchedulesLogic(
        schedules: List<Schedule>,
        currentTime: LocalDateTime
    ): Pair<Schedule?, Schedule?> {
        var currentScheduleResult: Schedule? = null
        var nextScheduleResult: Schedule? = null

        // Schedules from getScheduleByDate should already be sorted by time if DAO does it.
        // If not, sort them here:
        val sortedSchedules = schedules.sortedBy { it.startTime }

        currentScheduleResult = sortedSchedules.find { schedule ->
            // This check assumes schedules are only for the date of 'currentTime.toLocalDate()'
            // which is true if 'schedules' are 'schedulesForToday'
            if (schedule.date != currentTime.toLocalDate()) return@find false // Ensure task is for today

            val scheduleStartDateTime = schedule.date.atTime(schedule.startTime)
            val scheduleEndDateTime = schedule.date.atTime(schedule.endTime)
            !currentTime.isBefore(scheduleStartDateTime) && currentTime.isBefore(scheduleEndDateTime)
        }

        if (currentScheduleResult != null) {
            val currentScheduleEndDateTime = currentScheduleResult.date.atTime(currentScheduleResult.endTime)
            nextScheduleResult = sortedSchedules.find { schedule ->
                if (schedule.date != currentTime.toLocalDate()) return@find false

                val scheduleStartDateTime = schedule.date.atTime(schedule.startTime)
                // Next schedule starts after or at the same time current one ends, and is not the same schedule
                (!scheduleStartDateTime.isBefore(currentScheduleEndDateTime)) && schedule.id != currentScheduleResult.id
            }
        } else {
            // No current schedule, find the next upcoming schedule for today
            nextScheduleResult = sortedSchedules.find { schedule ->
                if (schedule.date != currentTime.toLocalDate()) return@find false

                val scheduleStartDateTime = schedule.date.atTime(schedule.startTime)
                scheduleStartDateTime.isAfter(currentTime)
            }
        }
        return Pair(currentScheduleResult, nextScheduleResult)
    }

    fun selectDateForDayView(date: LocalDate) {
        _selectedDateForDayView.value = date
    }

    // addSchedule now takes LocalDate and LocalTime as per your Schedule entity
    fun addSchedule(name: String, date: LocalDate, startTime: LocalTime, endTime: LocalTime) {
        viewModelScope.launch {
            if (endTime.isAfter(startTime)) { // Basic validation
                val newSchedule = Schedule(
                    name = name,
                    date = date,
                    startTime = startTime,
                    endTime = endTime
                )
                repository.insert(newSchedule)
            } else {
                // TODO: Expose error to UI (e.g., via a SharedFlow or another StateFlow)
            }
        }
    }

    // Updated sample data to use LocalDate and LocalTime, and inserts for a specific date
    private suspend fun insertSampleSchedulesForToday(dateForSamples: LocalDate) {
        val nowTime = LocalTime.now(ZoneId.systemDefault())
        val sampleSchedules = listOf(
            // Example: A schedule that should be "current" if app is run around nowTime
            Schedule(
                name = "Current Meeting (Sample)",
                date = dateForSamples,
                startTime = nowTime.minusMinutes(30),
                endTime = nowTime.plusMinutes(30)
            ),
            Schedule(
                name = "Lunch Break (Sample)",
                date = dateForSamples,
                startTime = nowTime.plusHours(1),
                endTime = nowTime.plusHours(2)
            ),
            Schedule(
                name = "Evening Review (Sample)",
                date = dateForSamples,
                startTime = nowTime.plusHours(3),
                endTime = nowTime.plusHours(4)
            )
        )
        sampleSchedules.forEach { repository.insert(it) }
    }
}
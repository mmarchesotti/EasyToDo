package com.mmarchesotti.easytodo.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class ScheduleRepository (private val scheduleDao: ScheduleDao) {

    fun getScheduleById(scheduleId: Long): Flow<Schedule?> {
        return scheduleDao.getScheduleById(scheduleId)
    }

    suspend fun insert(schedule: Schedule) {
        scheduleDao.insertSchedule(schedule)
    }

    suspend fun update(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule)
    }

    suspend fun delete(schedule: Schedule) {
        scheduleDao.deleteSchedule(schedule)
    }

    fun getScheduleByDate(date : LocalDate): Flow<List<Schedule>> {
        return scheduleDao.getScheduleByDate(date)
    }
}

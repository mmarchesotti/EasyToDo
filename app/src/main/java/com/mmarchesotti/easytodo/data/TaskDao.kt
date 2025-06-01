package com.mmarchesotti.easytodo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule): Long

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Query("SELECT * FROM schedules WHERE id = :scheduleId")
    fun getScheduleById(scheduleId: Long): Flow<Schedule?>

    @Query("SELECT * FROM schedules WHERE date = :date ORDER BY startTime ASC")
    fun getScheduleByDate(date: LocalDate): Flow<List<Schedule>>
}
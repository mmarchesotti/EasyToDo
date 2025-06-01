package com.mmarchesotti.easytodo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<Task?>

    @Query("SELECT * FROM tasks ORDER BY startTime ASC")
    fun getAllTasksSortedByStartTime(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE startTime >= :startOfDayMillis AND startTime < :endOfDayMillis ORDER BY startTime ASC")
    fun getTasksStartingOnDay(startOfDayMillis: Long, endOfDayMillis: Long): Flow<List<Task>>
}
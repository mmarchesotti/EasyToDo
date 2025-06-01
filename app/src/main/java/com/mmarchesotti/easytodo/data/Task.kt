package com.mmarchesotti.easytodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    var date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime
)
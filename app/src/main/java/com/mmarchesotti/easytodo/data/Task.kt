package com.mmarchesotti.easytodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val startTime: Long,
    val endTime: Long
)
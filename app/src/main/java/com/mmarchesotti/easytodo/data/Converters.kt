package com.mmarchesotti.easytodo.data // Ensure this package is correct

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime

class Converters {
    // LocalDate <-> Long (Epoch Day) Converters
    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    // LocalTime <-> Long (Nanoseconds of Day) Converters
    @TypeConverter
    fun fromNanosOfDay(value: Long?): LocalTime? {
        return value?.let { LocalTime.ofNanoOfDay(it) }
    }

    @TypeConverter
    fun localTimeToNanosOfDay(time: LocalTime?): Long? {
        return time?.toNanoOfDay()
    }
}
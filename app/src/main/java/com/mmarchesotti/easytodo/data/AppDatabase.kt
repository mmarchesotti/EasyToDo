package com.mmarchesotti.easytodo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Schedule::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "schedule_database"
                    )
                        // If you changed schema (like adding TypeConverters for existing columns of new types)
                        // AND you are on version 1, you MUST either:
                        // 1. Increment version and provide a migration (complex for now)
                        // 2. Uninstall the app from the device/emulator to clear the old DB.
                        // .fallbackToDestructiveMigration() // Option for development if you increment version
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
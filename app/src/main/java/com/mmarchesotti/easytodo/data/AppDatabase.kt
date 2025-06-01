package com.mmarchesotti.easytodo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
// entities = lists all the data classes that are database tables.
// version = database version, important for migrations. Start with 1.
// exportSchema = set to true if you want to export schema to a folder for version control (good for complex projects).
//                For simplicity now, 'false' is fine.
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        // @Volatile annotation ensures that the INSTANCE variable is always up-to-date
        // and the same to all execution threads. Its value will never be cached,
        // and all writes and reads will be done to and from the main memory.
        // It means that changes made by one thread to INSTANCE are visible to all other threads immediately.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            // synchronized block ensures that only one thread can execute this block of code at a time,
            // which is important for creating a singleton database instance.
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, // Use application context
                        AppDatabase::class.java,    // Your database class
                        "task_database"       // Name of the database file
                    )
                        // .fallbackToDestructiveMigration() // If you change schema and version, this would recreate db (lose data)
                        // Proper migrations are preferred for production apps.
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
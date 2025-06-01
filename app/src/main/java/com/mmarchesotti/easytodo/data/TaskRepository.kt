package com.mmarchesotti.easytodo.data

import kotlinx.coroutines.flow.Flow

class TaskRepository (private val taskDao: TaskDao) {

    val allTasks: Flow<List<Task>> = taskDao.getAllTasksSortedByStartTime()

    fun getTaskById(taskId: Long): Flow<Task?> {
        return taskDao.getTaskById(taskId)
    }

    suspend fun insert(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun update(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun delete(task: Task) {
        taskDao.deleteTask(task)
    }

    fun getTasksStartingOnDay(startOfDayMillis: Long, endOfDayMillis: Long): Flow<List<Task>> {
        return taskDao.getTasksStartingOnDay(startOfDayMillis, endOfDayMillis)
    }
}

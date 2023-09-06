package com.example.interriitapp.Utility

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Task(val title: String, val time: String)

class TaskManager(private val context: Context) {
    private val TASKS_PREFERENCES = "tasks_prefs"
    private val TASKS_KEY = "tasks_key"
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(TASKS_PREFERENCES, Context.MODE_PRIVATE)

    fun addTask(task: Task) {
        val tasks = getAllTasks().toMutableList()
        tasks.add(task)
        saveTasks(tasks)
    }
    fun removeTask(taskToRemove: Task) {
        val tasks = getAllTasks().toMutableList()
        tasks.removeIf { it.title == taskToRemove.title }
        saveTasks(tasks)
    }
    fun getAllTasks(): List<Task> {
        val tasksJson = sharedPreferences.getString(TASKS_KEY, null)
        return if (tasksJson != null) {
            val taskListType = object : TypeToken<List<Task>>() {}.type
            Gson().fromJson<List<Task>>(tasksJson, taskListType)
        } else {
            emptyList()
        }
    }
    private fun saveTasks(tasks: List<Task>) {
        val tasksJson = Gson().toJson(tasks)
        sharedPreferences.edit().putString(TASKS_KEY, tasksJson).apply()
    }
}

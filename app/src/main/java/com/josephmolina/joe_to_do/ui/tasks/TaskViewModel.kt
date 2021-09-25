package com.josephmolina.joe_to_do.ui.tasks

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.josephmolina.joe_to_do.data.PreferencesManager
import com.josephmolina.joe_to_do.data.SortOrder
import com.josephmolina.joe_to_do.data.Task
import com.josephmolina.joe_to_do.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val preferencesFlow = preferencesManager.preferencesFlow

    init {
        Log.d("task vm", "vm init")
        Log.d("task vm pref null", (preferencesFlow == null).toString())
    }
    /*
    One difference between LiveData and Flow is that LD only ever has the
    latest value of data whereas Flow has the stream of values

    Another difference is LD is lifecycle aware. Meaning when the fragment goes into the background
    LD detects this and stops sending events. Thus prevents crashes and memory leaks
    Flow has operators to manipulate the data, we can switch threads in the flow
    We don't lose any values because its the entire stream and not the latest value

    We often want to use Flow below the vm and then turn our data into LiveData so we can observe
    our LiveData in the fragment
     */

    // Holds a single value unlike normal flow that holds a stream of values
    val searchQuery = MutableStateFlow("")

    // Whenever the value of this flow changes - execute this block here
    // and the parameter we get passed is the current value of search query
    // use this value - get the tasks, which returns a flow and assign this result to the Task Flow

    // 2. When the user enters a new character this will trigger the flatmaplatest operator
    // and executes the taskDao.getTasks method passing in the current value of the search query
    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow
    ) { query, filterPreferences ->
        // What do we want to return ?
        Log.d("tasks vm", "combine query")

        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        Log.d("tasks vm", "flatmap latest")
        Log.d("tasks vm sortOrder is", filterPreferences.sortOrder.name)
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    // 3. After taskDao.getTasks is finished it will update the tasks value with whatever tasks it
    // found to match with the search query
    val tasks = tasksFlow.asLiveData()

    // This scope lives as long as the viewmodel does.
    // It will be canceled when the vm is cleared
    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }
    }

    fun onTaskSelected(task: Task) {

    }

    fun onTaskChecked(task: Task, checked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(isCompleted = checked))
    }

}
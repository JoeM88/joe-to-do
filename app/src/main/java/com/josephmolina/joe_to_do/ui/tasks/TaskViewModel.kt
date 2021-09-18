package com.josephmolina.joe_to_do.ui.tasks

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.josephmolina.joe_to_do.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TaskViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao
) : ViewModel() {

    init {
        Log.d("task vm","vm init")
    }

    val initDate = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

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
        sortOrder,
        hideCompleted
    ) { query, sortOrder, hideCompleted ->
        // What do we want to return ?
        Triple(query, sortOrder, hideCompleted)
    }.flatMapLatest { (query, sortOrder, hideCompleted) ->
        taskDao.getTasks(query, sortOrder, hideCompleted)
    }

    // 3. After taskDao.getTasks is finished it will update the tasks value with whatever tasks it
    // found to match with the search query
    val tasks = tasksFlow.asLiveData()
}

enum class SortOrder {
    BY_NAME,
    BY_DATE
}
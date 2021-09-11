package com.josephmolina.joe_to_do.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.josephmolina.joe_to_do.data.TaskDao

class TaskViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao
) : ViewModel() {
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
    val tasks = taskDao.getTasks().asLiveData()
}
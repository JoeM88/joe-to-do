package com.josephmolina.joe_to_do.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.josephmolina.joe_to_do.R
import com.josephmolina.joe_to_do.data.SortOrder
import com.josephmolina.joe_to_do.data.Task
import com.josephmolina.joe_to_do.databinding.FragmentTasksBinding
import com.josephmolina.joe_to_do.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnItemClickListener {

    // viewModel gets initialized whenever it is accessed
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d("frag", "onCreate")
    }

    override fun onPause() {
        super.onPause()
        Log.d("frag", "onPause")

    }

    override fun onStop() {
        super.onStop()
        Log.d("frag", "onStop")
    }
    // Calling bind because our layout is already inflated
    // Layoutinflation means the xml file is turned into an object
    // Since we pass it to the constructor we already have the xml object
    // So we don;t need to inflate it we can just pass the view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("frag", "onviewcreated")

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TaskAdapter(this)
        binding.apply {
            taskRecyclerview.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true) // if we know the dimensions
            }
        }

        // The viewlifecycle owner helps the livedata determine if fragment is active
        // Because fragments have 2 lifecycles. It has its own and the activity that it is contained into.
        Log.d("frag", "about to observe")
        // 2nd parameters is a lanmbda function that gets called when a new update is available
        viewModel.tasks.observe(viewLifecycleOwner, {
            Log.d("tasks vm", "observe method submitting list")
            taskAdapter.submitList(it)
        })
    }


    override fun onResume() {
        super.onResume()
        Log.d("frag", "onResume")

    }
    // This is where we inflate/ activate our options menu.
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflator turns options menu resource file into an menu object
        inflater.inflate(R.menu.menu_fragment_tasks, menu)

        // Get reference to searchview
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        // 1. This is where searching begins. The user will type something into the searchview
        // This in returns sets a new value to the viewmodels searchQuery
        searchView.onQueryTextChanged {
            // Update  search query
            viewModel.searchQuery.value = it
        }

        /*
        How we launch a
        Corotine scope that lives as long as the view from the fragment this
        Remember we want to stop all work when the fragment is not visible anymore
        because we dont need it anymore.

         */
        viewLifecycleOwner.lifecycleScope.launch {

            // We call collect when we want to read the latest value
            // whenever a new value is given then this collect method is called

            // First only reads the latest value
            // Setting whether the checkmark should be shown or not
            Log.d("tasks vm isChecked", viewModel.preferencesFlow.first().hideCompleted.toString())

            menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                viewModel.preferencesFlow.first().hideCompleted
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                // We have to return true to indicate we handled the item
                // if we return false the system will handle it instead
                 true
            }

            R.id.action_sort_by_date_created -> {
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                true
            }

            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                /*
                Notice how we didnt name this updateHideCompleted
                Because if we did then calling the function here would
                imply that the fragment does this operation. When according to MVVM
                the fragment should only handle UI not logic.

                THis why we pass it to vm and specify this with the name.
                 */
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }

            R.id.action_delete_all_completed_tasks -> {
                true
            }

            else -> {
                // returns false internally - meaning we didnt handle the click
                super.onOptionsItemSelected(item)
            }
        }
    }

    // Delegate work to vm
    // Ambigious names so we vm has the responsibility of designing whatit wants to do
    override fun onItemClick(task: Task) {
        viewModel.onTaskSelected(task)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskChecked(task, isChecked)
    }
}
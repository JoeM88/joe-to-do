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
import androidx.recyclerview.widget.LinearLayoutManager
import com.josephmolina.joe_to_do.R
import com.josephmolina.joe_to_do.databinding.FragmentTasksBinding
import com.josephmolina.joe_to_do.util.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    // viewModel gets initialized whenever it is accessed
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Log.d("frag", "onCreate")
    }

    // Calling bind because our layout is already inflated
    // Layoutinflation means the xml file is turned into an object
    // Since we pass it to the constructor we already have the xml object
    // So we don;t need to inflate it we can just pass the view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("frag", "onviewcreated")

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TaskAdapter()
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
            taskAdapter.submitList(it)
        })

        viewModel.hideCompleted.asLiveData().observe(viewLifecycleOwner, {
            Toast.makeText(context, "hiding", Toast.LENGTH_SHORT).show()
        })
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
            // Update search query
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.sortOrder.value = SortOrder.BY_NAME
                // We have to return true to indicate we handled the item
                // if we return false the system will handle it instead
                 true
            }

            R.id.action_sort_by_date_created -> {
                viewModel.sortOrder.value = SortOrder.BY_DATE
                true
            }

            R.id.action_hide_completed_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
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
}
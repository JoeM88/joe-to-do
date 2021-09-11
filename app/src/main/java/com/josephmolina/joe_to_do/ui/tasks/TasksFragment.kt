package com.josephmolina.joe_to_do.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.josephmolina.joe_to_do.R
import com.josephmolina.joe_to_do.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    //
    private val viewModel: TaskViewModel by viewModels()

    // Calling bind because our layoutis already inflated
    // Layoutinflation means the xml file is turned into an object
    // Since we pass it to the constructor we already have the xml object
    // So we don;t need to inflate it we can just pass the view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        //
        // 2nd parameters is a lanmbda function that gets called when a new update is available
        viewModel.tasks.observe(viewLifecycleOwner, {
            taskAdapter.submitList(it)
        })
    }
}
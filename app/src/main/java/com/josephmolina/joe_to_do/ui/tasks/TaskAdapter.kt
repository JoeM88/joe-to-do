package com.josephmolina.joe_to_do.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.josephmolina.joe_to_do.data.Task
import com.josephmolina.joe_to_do.databinding.ItemTaskBinding

// List Adapter is great for reactive lists
class TaskAdapter : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    // Pass in a binding object that is created from layouts.
    class TaskViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
        // One of the good things about view binding is that it is compile time safe
        // Another is we can guarantee the view we are accessing is from that particular file and not in another.
        fun bind(task: Task) {
            binding.apply {
                checkboxCompleted.isChecked = task.isCompleted
                name.text = task.name
                name.paint.isStrikeThruText = task.isCompleted
                priority.isVisible = task.isImportant
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        // This creates the actual view holder.
        // We know it takes in a binding object - so let's create it
        val inflator = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflator, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = getItem(position)
        holder.bind(currentTask)
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        // Two items represent the same logic item
        // For example an item could just change position in the list without changing its contents
        // THis tells it when it needs to move the item around
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        //When the contents within an item has changed
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}

// A small class where that knows where the single views are in our layout are and what data it should put

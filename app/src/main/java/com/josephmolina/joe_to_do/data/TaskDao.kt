package com.josephmolina.joe_to_do.data

import androidx.room.*
import com.josephmolina.joe_to_do.ui.tasks.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao // Triggers code generation for DAO operations
interface TaskDao {
    // Allows us to use the method into a suspending function
    // Kotlin coroutines
    // Allows us ot switch to a different thread
    // Since this is an io operation we need to do it in a different thread
    // Room does not allow us to make any operations on the main thread.
    // Suspend function can be paused and resumed
    // If we insert a task that has the same id as another then we replace it
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    // Flow represents a stream of data
    // We will not get one task but a stream of tasks.

    // FLow can only be used in a corotuine
    // Room handled threading.
    // No need for suspend?
    // Flow is Asynchornosuly stream of data
    // LIKE -- It doesnt have to be an exact match but the searchquery must be in the string somwhere
    // || == append
    // '%' || :searchQuery || '%'  -- tells sqlite to check rows where the searchquery exists anywhere in the name
    // it can be in the front, middle or end it doesnt matter as long as it is there
    // Removing the first '%' would mean that the string needs to start with the search query in order to match

    // 0 means false
    // Important tasks will be at top

    fun getTasks(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when (sortOrder) {
            SortOrder.BY_DATE -> getTasksSortedByDateCreated(query, hideCompleted)
            SortOrder.BY_NAME -> getTasksSortedByName(query, hideCompleted)
        }

    @Query("SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, name")
    fun getTasksSortedByName(searchQuery: String, hideCompleted:Boolean ): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (isCompleted != :hideCompleted OR isCompleted = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, created")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Task>>
}
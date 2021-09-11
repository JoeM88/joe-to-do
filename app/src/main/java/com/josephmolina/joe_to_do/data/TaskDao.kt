package com.josephmolina.joe_to_do.data

import androidx.room.*
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
    @Query("SELECT * FROM task_table")
    fun getTasks(): Flow<List<Task>>
}
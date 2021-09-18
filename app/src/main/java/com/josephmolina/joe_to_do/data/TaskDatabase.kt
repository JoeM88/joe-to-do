package com.josephmolina.joe_to_do.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.josephmolina.joe_to_do.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

// Abstract because Room generates implementation via @Database annotation
@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    // Get handle to task dao to perform db operations
    abstract fun taskDao() : TaskDao

    // Gets launched the first time the db is created.
    // Not to be confused for when the app is launched.
    // It is only ever created once an every other app launch wont re-create it.
    class Callback @Inject constructor(
            //With type provider - the field is lazily injected. - not created until we call .get()
            private val database: Provider<TaskDatabase>,
            @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // DB operations
            val dao = database.get().taskDao()

            applicationScope.launch {
                dao.insert(Task("Finish Android course"))
                dao.insert(Task("Get groceries"))
                dao.insert(Task("Watch Candyman movie", isImportant = true))
                dao.insert(Task("Talk to Elon Musk", isCompleted = true))
                dao.insert(Task("Eat cupcake"))
                dao.insert(Task("Run 1 mile", isCompleted = true))
            }
        }
    }
}
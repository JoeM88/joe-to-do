package com.josephmolina.joe_to_do.di

import android.app.Application
import androidx.room.Room
import com.josephmolina.joe_to_do.data.TaskDao
import com.josephmolina.joe_to_do.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

// object makes this class a Singleton

/*
The Module tells Dagger how to create the dependencies
By default, all bindings in Dagger are “unscoped”.
 This means that each time the binding is requested,
  Dagger will create a new instance of the binding.

  Dagger also lets us scope to a component. A Scoped binding
  will only be created once per instance of the component that its scoped to.
  All other requests for that binding will share the same instance
 */
@Module
// THis is the scope. It means these components will live as long as the application does.
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton //Only ever create one instance of our db
    fun provideDatabase(app: Application,
                        callback: TaskDatabase.Callback) =
                    Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
                    .fallbackToDestructiveMigration() // drops table and creates new one
                    .addCallback(callback)
                    .build() // creates one instance of our db

    @Provides
    fun providesTaskDao(taskDatabase: TaskDatabase) = taskDatabase.taskDao()

    // By default Coroutine Gets canceled when any of its child fails
    // Supervisor job keeps them going.
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

// If we have more than one method of the same type
// hilt won't know what to return. For this, we create a qualifier
// as a way to identify which object we want to return correctly
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
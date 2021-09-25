package com.josephmolina.joe_to_do.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder {
    BY_NAME,
    BY_DATE
}

/*
Since we can only return one value from a mapping function we use this class
to encapsulate our values
 */
data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    init {
        Log.d("Preference Manager", "init")
    }

    private val dataStore = context.createDataStore("user_preferences")

    private object PreferenceKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }

    // Suspend because it is an IO operation.
    // Flows always run in a corotine
    // Remember you must call these functions from a coroutine
    suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            Log.d("tasks vm", "update SortOrder")
            preferences[PreferenceKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean) {
        dataStore.edit { preferences ->
            Log.d("tasks vm", "updated hideCompleted")
            preferences[PreferenceKeys.HIDE_COMPLETED] = hideCompleted
        }
    }

    // Can only store primitive types
    val preferencesFlow = dataStore.data
        .catch { exception ->
            // Means something went wrong while reading the data
            if (exception is IOException) {
                Log.e(TAG, "Error reading preferences: ", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            /*
            Gets the string value of whatever the current Sort Order value is inside preferences.
            preferences[PreferencesKeys.SORT_ORDER] returns either BY_NAME or BY_DATE
            ?: If there is no value we default back to filtering by date (BY_DATE)
             */
            val sortOrder = SortOrder.valueOf(
                preferences[PreferenceKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted = preferences[PreferenceKeys.HIDE_COMPLETED] ?: false
            Log.d("tasks vm", "retuning back new filterPreferences")
            FilterPreferences(sortOrder, hideCompleted)
        }
}

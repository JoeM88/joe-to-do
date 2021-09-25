package com.josephmolina.joe_to_do.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

/*
Created value is set to the current time in
milliseconds when the obj is created

This annotation declares a table for the object.
Room creates a table named "task_table"

Value does not remain at 0 but will be increased by Room.
 */
@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    val isImportant: Boolean = false,
    val isCompleted: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val createdDateFormatted: String get() = DateFormat.getDateTimeInstance().format(created)
}
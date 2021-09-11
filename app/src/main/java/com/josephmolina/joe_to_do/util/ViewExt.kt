package com.josephmolina.joe_to_do.util

import androidx.appcompat.widget.ForwardingListener
import androidx.appcompat.widget.SearchView

// Extension functions for views

/*
 We can just call SearchView.onQueryTextChanged from the activity/fragment
 To this function we pass another function where we define what we want to happen
 when the query text changes.

 This has some performance updates

 */
inline fun SearchView.onQueryTextChanged(crossinline listener: (String) -> Unit ){
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        // Triggered each time the user enters a character
        override fun onQueryTextChange(newText: String?): Boolean {
            listener(newText.orEmpty())
            return true
        }
    })
}
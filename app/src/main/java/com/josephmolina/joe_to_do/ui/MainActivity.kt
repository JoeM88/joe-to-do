package com.josephmolina.joe_to_do.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.josephmolina.joe_to_do.R
import com.josephmolina.joe_to_do.ui.tasks.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
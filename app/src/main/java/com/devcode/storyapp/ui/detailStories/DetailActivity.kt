package com.devcode.storyapp.ui.detailStories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.devcode.storyapp.R

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
    }

companion object {
    const val EXTRA_STATE = "extra_state"
    }
}
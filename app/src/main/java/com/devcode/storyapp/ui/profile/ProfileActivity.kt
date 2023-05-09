package com.devcode.storyapp.ui.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bumptech.glide.Glide
import com.devcode.storyapp.R
import com.devcode.storyapp.databinding.ActivityDetailBinding
import com.devcode.storyapp.databinding.ActivityProfileBinding
import com.devcode.storyapp.db.ListStoryItem
import com.devcode.storyapp.ui.detailStories.DetailActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupView() {
        val story = intent.getParcelableExtra<ListStoryItem>(DetailActivity.EXTRA_STATE) as ListStoryItem
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.profileImage)
        binding.usernameAccount.text = story.name
    
    }

    companion object {
        const val EXTRA_STATE = "extra_state"
    }
}
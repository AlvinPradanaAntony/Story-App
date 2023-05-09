package com.devcode.storyapp.ui.home

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.devcode.storyapp.R
import com.devcode.storyapp.ViewModelFactory
import com.devcode.storyapp.adapater.StoryAdapter
import com.devcode.storyapp.databinding.ActivityMainBinding
import com.devcode.storyapp.db.ListStoryItem
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.ui.detailStories.DetailActivity
import com.devcode.storyapp.ui.login.LoginActivity
import com.devcode.storyapp.ui.login.Session
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var userToken: String
    private val list = ArrayList<ListStoryItem>()
    private val adapter: StoryAdapter by lazy {
        StoryAdapter(list)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        observeLoading()
        observeErrorMessage()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            binding.txtUsernameAccount.text = user.name.replaceFirstChar { it.uppercase() }
            userToken = user.token
            stories(user.token)
        }
    }

    private fun stories(token: String) {
        mainViewModel.postStory(token)
        mainViewModel.isStory.observe(this) { user ->
            if (user != null) {
                adapter.setListStory(user.listStory)
                setRecycleView()
            }
        }
    }
    override fun onResume() {
        super.onResume()
        stories(userToken)
    }

    private fun setRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager
        binding.rvStories.setHasFixedSize(true)
        binding.rvStories.adapter = adapter
        setupAction()
    }

    private fun setupAction() {
        adapter.setOnItemClickCallback(object : StoryAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_STATE, data)
                startActivity(intent)
            }
        })
    }

    private fun observeLoading() {
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun observeErrorMessage() {
        mainViewModel.isError.observe(this) {
            showSnackBar(it)
        }
    }

    private fun showSnackBar(value: String) {
        Snackbar.make(binding.root, value, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
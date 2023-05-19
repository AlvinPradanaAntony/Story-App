package com.devcode.storyapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devcode.storyapp.R
import com.devcode.storyapp.ViewModelFactory
import com.devcode.storyapp.adapter.LoadingStateAdapter
import com.devcode.storyapp.adapter.PagingStoryAdapter
import com.devcode.storyapp.databinding.ActivityMainBinding
import com.devcode.storyapp.ui.addStory.AddStoryActivity
import com.devcode.storyapp.ui.mapList.MapListActivity
import com.devcode.storyapp.ui.profile.ProfileActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var adapter: PagingStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setRecycleView()
        stories()
        setupAction()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            val name = user.name
            binding.txtUsernameAccount.text = name.replaceFirstChar { it.uppercase() }
            Glide.with(this)
                .load("https://ui-avatars.com/api/?name=$name&size=128&background=random")
                .placeholder(R.drawable.ic_placeholder_photo)
                .error(R.drawable.ic_placeholder_photo)
                .into(binding.ivPhoto)
        }
    }

    private fun stories() {
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        mainViewModel.getStory().observe(this){
            adapter.submitData(lifecycle, it)
            setRecycleView()
        }
    }
    override fun onResume() {
        super.onResume()
        stories()
    }

    private fun setRecycleView() {
        val layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = PagingStoryAdapter()
        binding.rvStories.layoutManager = layoutManager
        binding.rvStories.setHasFixedSize(true)
    }

    private fun setupAction() {
        binding.mapList.setOnClickListener {
            startActivity(Intent(this, MapListActivity::class.java))
        }
        binding.setLanguage.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.ivPhoto.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        binding.favButton.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            stories()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }



    private fun showSnackBar(value: String) {
        Snackbar.make(binding.root, value, Snackbar.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
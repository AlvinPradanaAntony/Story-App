package com.devcode.storyappfinal.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.devcode.storyappfinal.R
import com.devcode.storyappfinal.ViewModelFactory
import com.devcode.storyappfinal.adapter.LoadingStateAdapter
import com.devcode.storyappfinal.adapter.PagingStoryAdapter
import com.devcode.storyappfinal.databinding.ActivityMainBinding
import com.devcode.storyappfinal.ui.addStory.AddStoryActivity
import com.devcode.storyappfinal.ui.mapList.MapListActivity
import com.devcode.storyappfinal.ui.profile.ProfileActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var factory: ViewModelFactory
    private lateinit var adapter: PagingStoryAdapter
    private var currentScrollPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView()
        setupViewModel()
        getStories()
        setupAction()
    }

    private fun recyclerView() {
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.setHasFixedSize(true)
        adapter = PagingStoryAdapter()
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

    private fun getStories() {
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )
        currentScrollPosition = (binding.rvStories.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        mainViewModel.getStory().observe(this){
            adapter.submitData(lifecycle, it)
            binding.rvStories.scrollToPosition(0)
        }
    }
    override fun onResume() {
        super.onResume()
        recyclerView()
        getStories()
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
            recyclerView()
            getStories()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}
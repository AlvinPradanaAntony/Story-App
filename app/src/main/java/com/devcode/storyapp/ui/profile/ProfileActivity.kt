package com.devcode.storyapp.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.devcode.storyapp.R
import com.devcode.storyapp.ViewModelFactory
import com.devcode.storyapp.databinding.ActivityProfileBinding
import com.devcode.storyapp.ui.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile"
        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        val factory = ViewModelFactory.getInstance(this)
        profileViewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]

        profileViewModel.getUser().observe(this) { user ->
            Glide.with(this)
                .load("https://ui-avatars.com/api/?name=${user.name}&size=128&background=random")
                .placeholder(R.drawable.ic_placeholder_photo)
                .error(R.drawable.ic_placeholder_photo)
                .into(binding.profileImage)
            binding.apply {
                usernameAccount.text = user.name
                emailAccount.text = user.email
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun setupAction() {
        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
            profileViewModel.logout()
        }
    }
}
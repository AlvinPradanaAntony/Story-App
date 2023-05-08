package com.devcode.storyapp.ui.home

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.devcode.storyapp.R
import com.devcode.storyapp.ViewModelFactory
import com.devcode.storyapp.databinding.ActivityMainBinding
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.ui.login.LoginActivity
import com.devcode.storyapp.ui.login.Session

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if(!user.isLogin){
                startActivity(Intent(this, LoginActivity::class.java))
            }
            binding.txtUsernameAccount.text = getString(R.string.greeting, user.name)
        }
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            mainViewModel.logout()
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
    }
}
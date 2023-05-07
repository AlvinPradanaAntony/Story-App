package com.devcode.storyapp.ui.login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.devcode.storyapp.R
import com.devcode.storyapp.ViewModelFactory
import com.devcode.storyapp.databinding.ActivityLoginBinding
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.ui.register.RegisterActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var user: UserModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        customLogo()
        setupViewModel()
        validation()
        binding.txtRegisternow.setOnClickListener() {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }


    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this) { user ->
            this.user = user
        }
    }

    private fun validation() {
        binding.buttonLogin.setOnClickListener {
            val email = binding.edLoginEmail.text?.trim().toString()
            val password = binding.edLoginPassword.text?.trim().toString()

            if (email.isEmpty() && password.isEmpty()) {
                AlertDialog.Builder(this).apply {
                    setTitle("Oops!")
                    setMessage("Email dan password tidak boleh kosong")
                    setPositiveButton("OK") { _, _ -> }
                    create()
                    show()
                }
            } else{
                if (email.isEmpty()){
                    binding.edLoginEmail.error = "Input Email Cannot be Empty"
                    binding.edLoginEmail.requestFocus()
                } else if(password.isEmpty()){
                    binding.edLoginPassword.error = "Input Password Cannot be Empty"
                    binding.edLoginPassword.requestFocus()
                } else if (!isValidEmail(email)){
                    binding.edLoginEmail.error = resources.getString(R.string.email_invalid)
                    binding.edLoginEmail.requestFocus()
                } else if(password.length<8){
                    binding.edLoginPassword.error = resources.getString(R.string.password_minimum_character)
                    binding.edLoginPassword.requestFocus()
                } else{
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                 }
            }
        }
    }
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun customLogo(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.frameLayout.outlineAmbientShadowColor = getColor(R.color.shadowColor)
            binding.frameLayout.outlineSpotShadowColor = getColor(R.color.shadowColor)
        } else {
            binding.frameLayout.elevation = 6f
        }
    }
}
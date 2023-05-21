package com.devcode.storyapp.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.devcode.storyapp.R
import com.devcode.storyapp.ViewModelFactory
import com.devcode.storyapp.databinding.ActivityLoginBinding
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.ui.main.MainActivity
import com.devcode.storyapp.ui.register.RegisterActivity
import com.devcode.storyapp.utils.Result
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customLogo()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun playAnimation(){
        val email = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(325)
        val password = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(325)
        val btn_login = ObjectAnimator.ofFloat(binding.buttonLogin, View.ALPHA, 1f).setDuration(325)
        val sugges = ObjectAnimator.ofFloat(binding.suggestRegister, View.ALPHA, 1f).setDuration(325)

        AnimatorSet().apply {
            playSequentially(email, password, btn_login, sugges)
            startDelay = 325
        }.start()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
    }

    private fun setupAction() {
        binding.txtRegisternow.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.buttonLogin.setOnClickListener {
            val email = binding.edLoginEmail.text?.trim().toString()
            val password = binding.edLoginPassword.text?.trim().toString()

            if (email.isEmpty() && password.isEmpty()) {
                AlertDialog.Builder(this).apply {
                    setTitle("Oops!")
                    setMessage(R.string.email_n_password_empty)
                    setPositiveButton("OK") { _, _ -> }
                    create()
                    show()
                }
            } else {
                if (email.isEmpty()) {
                    binding.edLoginEmail.error = resources.getString(R.string.email_empty)
                    binding.edLoginEmail.requestFocus()
                } else if (password.isEmpty()) {
                    binding.edLoginPassword.error = resources.getString(R.string.password_empty)
                    binding.edLoginPassword.requestFocus()
                } else if (!isValidEmail(email)) {
                    binding.edLoginEmail.error = resources.getString(R.string.email_invalid)
                    binding.edLoginEmail.requestFocus()
                } else if (password.length < 8) {
                    binding.edLoginPassword.error = resources.getString(R.string.password_minimum_character)
                    binding.edLoginPassword.requestFocus()
                } else {
                    binding.edLoginEmail.clearFocus()
                    binding.edLoginPassword.clearFocus()
                    hideKeyboard()
                    login(email, password)
                }
            }
        }
    }

    private fun login(email: String, password: String) {
        showLoading(true)
        loginViewModel.doingLogin(email, password).observe(this) { user ->
            when (user) {
                is Result.Success -> {
                    showLoading(false)
                    val response = user.data
                    val name = response.loginResult?.name.toString()
                    val token = response.loginResult?.token.toString()
                    saveUserData(UserModel(name, email, token, true))
                    Log.d("CekTokenLogin", "Token: $token - Email: $email - Name: $name")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                is Result.Loading -> showLoading(true)
                is Result.Error -> {
                    showSnackBar(user.error)
                    showLoading(false)
                }
                else -> {
                    showSnackBar("Something went wrong")
                    showLoading(false)
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun customLogo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.frameLayout.outlineAmbientShadowColor = getColor(R.color.shadowColor)
            binding.frameLayout.outlineSpotShadowColor = getColor(R.color.shadowColor)
        } else {
            binding.frameLayout.elevation = 6f
        }
    }

    private fun saveUserData(user: UserModel) {
        loginViewModel.saveUser(user)
    }

    private fun showSnackBar(value: String) {
        Snackbar.make(
            binding.root, value, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.overlayBg.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
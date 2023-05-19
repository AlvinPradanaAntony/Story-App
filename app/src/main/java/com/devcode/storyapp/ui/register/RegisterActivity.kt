package com.devcode.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.devcode.storyapp.R
import com.devcode.storyapp.ViewModelFactory
import com.devcode.storyapp.databinding.ActivityRegisterBinding
import com.devcode.storyapp.model.UserModel
import com.devcode.storyapp.model.UserPreferences
import com.devcode.storyapp.ui.home.MainActivity
import com.devcode.storyapp.ui.login.LoginActivity
import com.devcode.storyapp.ui.login.LoginViewModel
import com.devcode.storyapp.utils.Result
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customLogo()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupViewModel() {
        factory = ViewModelFactory.getInstance(this)
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]
    }

    private fun playAnimation(){
        val name = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(325)
        val email = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(325)
        val password = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(325)
        val confirm = ObjectAnimator.ofFloat(binding.edRegisterConfirmPass, View.ALPHA, 1f).setDuration(325)
        val btn_register = ObjectAnimator.ofFloat(binding.buttonRegister, View.ALPHA, 1f).setDuration(325)
        val sugges = ObjectAnimator.ofFloat(binding.suggestLogin, View.ALPHA, 1f).setDuration(325)

        AnimatorSet().apply {
            playSequentially(name, email, password, confirm, btn_register, sugges)
            startDelay = 325
        }.start()
    }

    private fun setupAction() {
        binding.txtLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
        binding.buttonRegister.setOnClickListener {
            val fullName = binding.edRegisterName.text?.trim().toString()
            val emailRegister = binding.edRegisterEmail.text?.trim().toString()
            val passwordRegister = binding.edRegisterPassword.text?.trim().toString()
            val confirmPasswordRegister = binding.edRegisterConfirmPass.text?.trim().toString()

            if (fullName.isEmpty() && emailRegister.isEmpty() && passwordRegister.isEmpty()) {
                AlertDialog.Builder(this).apply {
                    setTitle("Oops!")
                    setMessage(R.string.email_n_password_empty)
                    setPositiveButton("OK") { _, _ -> }
                    create()
                    show()
                }
            } else {
                if (fullName.isEmpty()) {
                    binding.edRegisterName.error = resources.getString(R.string.full_name_empty)
                    binding.edRegisterName.requestFocus()
                } else if (emailRegister.isEmpty()) {
                    binding.edRegisterEmail.error = resources.getString(R.string.email_empty)
                    binding.edRegisterEmail.requestFocus()
                } else if (passwordRegister.isEmpty()) {
                    binding.edRegisterPassword.error = resources.getString(R.string.password_empty)
                    binding.edRegisterPassword.requestFocus()
                } else if (confirmPasswordRegister.isEmpty()) {
                    binding.edRegisterConfirmPass.error = resources.getString(R.string.password_confirmation_empty)
                    binding.edRegisterConfirmPass.requestFocus()
                } else if (!isValidEmail(emailRegister)) {
                    binding.edRegisterEmail.error = resources.getString(R.string.email_invalid)
                    binding.edRegisterEmail.requestFocus()
                } else if (passwordRegister.length < 8) {
                    binding.edRegisterPassword.error =
                        resources.getString(R.string.password_minimum_character)
                    binding.edRegisterPassword.requestFocus()
                } else if (confirmPasswordRegister != passwordRegister) {
                    binding.edRegisterConfirmPass.error = resources.getString(R.string.password_not_match)
                    binding.edRegisterConfirmPass.requestFocus()
                } else {
                    binding.edRegisterName.clearFocus()
                    binding.edRegisterEmail.clearFocus()
                    binding.edRegisterPassword.clearFocus()
                    hideKeyboard()
                    register(fullName, emailRegister, passwordRegister)
                }
            }
        }
    }

    private fun register(name:String, email: String, password: String) {
        showLoading(true)
        registerViewModel.doingRegister(name, email, password).observe(this) { user ->
            when (user) {
                is Result.Success -> {
                    showLoading(false)
                    AlertDialog.Builder(this@RegisterActivity).apply {
                        setTitle("Yeah!")
                        setMessage(R.string.account_already_registered)
                        setPositiveButton("Continue") { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
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

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun customLogo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.frameLayout.outlineAmbientShadowColor = getColor(R.color.shadowColor)
            binding.frameLayout.outlineSpotShadowColor = getColor(R.color.shadowColor)
        } else {
            binding.frameLayout.elevation = 6f
        }
    }

    private fun showSnackBar(value: String) {
        Snackbar.make(
            binding.buttonRegister, value, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.overlayBg.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
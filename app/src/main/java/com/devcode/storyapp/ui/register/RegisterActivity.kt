package com.devcode.storyapp.ui.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.devcode.storyapp.R
import com.devcode.storyapp.databinding.ActivityRegisterBinding
import com.devcode.storyapp.ui.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customLogo()
        validation()
        binding.txtLogin.setOnClickListener() {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun setupViewModel() {

    }

    private fun validation() {
        binding.buttonRegister.setOnClickListener {
            val fullName = binding.edRegisterName.text?.trim().toString()
            val emailRegister = binding.edRegisterEmail.text?.trim().toString()
            val passwordRegister = binding.edRegisterPassword.text?.trim().toString()
            val confirmPasswordRegister = binding.edRegisterConfirmPass.text?.trim().toString()

            if (fullName.isEmpty() && emailRegister.isEmpty() && passwordRegister.isEmpty()) {
                AlertDialog.Builder(this).apply {
                    setTitle("Oops!")
                    setMessage("Semua Inputan tidak boleh kosong")
                    setPositiveButton("OK") { _, _ -> }
                    create()
                    show()
                }
            } else {
                if (fullName.isEmpty()) {
                    binding.edRegisterName.error = "Input FullName Cannot be Empty"
                    binding.edRegisterName.requestFocus()
                } else if (emailRegister.isEmpty()) {
                    binding.edRegisterEmail.error = "Input Email Cannot be Empty"
                    binding.edRegisterEmail.requestFocus()
                } else if (passwordRegister.isEmpty()) {
                    binding.edRegisterPassword.error = "Input Password Cannot be Empty"
                    binding.edRegisterPassword.requestFocus()
                } else if (confirmPasswordRegister.isEmpty()) {
                    binding.edRegisterConfirmPass.error = "Input Confirm Pass Cannot be Empty"
                    binding.edRegisterConfirmPass.requestFocus()
                } else if (!isValidEmail(emailRegister)) {
                    binding.edRegisterEmail.error = resources.getString(R.string.email_invalid)
                    binding.edRegisterEmail.requestFocus()
                } else if (passwordRegister.length < 8) {
                    binding.edRegisterPassword.error =
                        resources.getString(R.string.password_minimum_character)
                    binding.edRegisterPassword.requestFocus()
                } else if (confirmPasswordRegister != passwordRegister) {
                    binding.edRegisterConfirmPass.error = "Password Tidak Sama"
                    binding.edRegisterConfirmPass.requestFocus()
                } else {
                    Toast.makeText(this, "Daftar Berhasil", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
}
package com.devcode.storyapp.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.WindowInsets
import android.view.WindowManager
import com.devcode.storyapp.R
import com.devcode.storyapp.databinding.ActivityLoginBinding
import com.devcode.storyapp.databinding.ActivityMainBinding
import com.devcode.storyapp.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            binding.frameLayout.outlineAmbientShadowColor = getColor(R.color.shadowColor)
            binding.frameLayout.outlineSpotShadowColor = getColor(R.color.shadowColor)
        } else {
            binding.frameLayout.elevation = 6f
        }

        binding.txtRegisternow.setOnClickListener() {
            startActivity(Intent(this,RegisterActivity::class.java))
        }
    }

}
package com.devcode.storyapp.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.devcode.storyapp.MainActivity
import com.devcode.storyapp.R
import com.devcode.storyapp.databinding.ActivitySplashBinding


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val versionapp: String = getVersionApp()
        textView = binding.txtVersionapp
        textView.text = "v$versionapp"

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, delay)
    }

    private fun getVersionApp(): String {
        var myVersionName = "Beta Version"
        try {
            val myVersion = packageManager.getPackageInfo(packageName, 0)
            myVersionName = myVersion.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return myVersionName
    }

    companion object{
        const val delay = 3000L
    }
}
package com.devcode.storyapp.ui.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.devcode.storyapp.databinding.ActivitySplashBinding
import com.devcode.storyapp.ui.login.LoginActivity


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getVersionApp()
        customSpanTitleLogo()
        playAnimation()
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, delaySplashScreen)
    }

    private fun getVersionApp() {
        val txtVersion = binding.txtVersionapp
        var myVersionName = "Beta Version"
        try {
            val myVersion = packageManager.getPackageInfo(packageName, 0)
            myVersionName = myVersion.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        txtVersion.text = "v$myVersionName"
    }

    private fun customSpanTitleLogo(){
        val txtLogo = binding.txtLogo
        val spannableString = SpannableString("StoryApp")
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#F79738")), 5, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtLogo.text = spannableString
    }
    private fun playAnimation() {
        val alphaAnimation = AlphaAnimation(0f, 1f)
        alphaAnimation.duration = 1000
        binding.imageView.startAnimation(alphaAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            binding.txtLogo.visibility = TextView.VISIBLE
            val text = ObjectAnimator.ofFloat(binding.txtLogo, View.ALPHA, 1f).setDuration(500)
            AnimatorSet().apply {
                playSequentially(text)
                start()
            }
            ObjectAnimator.ofFloat(binding.txtLogo, View.TRANSLATION_X, -30f, 0f).apply {
                duration = 500
            }.start()
        }, delayAnimation)
    }

    companion object{
        const val delaySplashScreen = 6000L
        const val delayAnimation = 2500L
    }
}
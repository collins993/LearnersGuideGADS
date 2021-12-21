package io.github.collins993.learnersguide.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.asLiveData
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.databinding.ActivityDashBoardBinding
import io.github.collins993.learnersguide.databinding.ActivityMainBinding
import io.github.collins993.learnersguide.ui.authentication.LoginActivity
import io.github.collins993.learnersguide.ui.onboarding.screens.ViewPagerActivity
import io.github.collins993.learnersguide.utils.UserManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userManager: UserManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userManager = UserManager(this)

        Handler().postDelayed({
            onBoardingFinished()
            finish()
        },3000)

    }

    private fun onBoardingFinished() {
        userManager.isFinishedFlow.asLiveData().observe(this, {
            if (it) {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            else{
                startActivity(Intent(this, ViewPagerActivity::class.java))
            }
        })
    }
}
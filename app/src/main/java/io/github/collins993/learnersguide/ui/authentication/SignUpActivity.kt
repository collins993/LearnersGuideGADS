package io.github.collins993.learnersguide.ui.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.databinding.ActivityLoginBinding
import io.github.collins993.learnersguide.databinding.ActivityMainBinding
import io.github.collins993.learnersguide.databinding.ActivitySignUpBinding
import io.github.collins993.learnersguide.ui.dashboard.DashBoardActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this, DashBoardActivity::class.java))
        }

        binding.signupLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
package io.github.collins993.learnersguide.ui.authentication

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.databinding.ActivityLoginBinding
import io.github.collins993.learnersguide.databinding.ActivityMainBinding
import io.github.collins993.learnersguide.databinding.ActivitySignUpBinding
import io.github.collins993.learnersguide.ui.CreateProfileActivity
import io.github.collins993.learnersguide.ui.dashboard.DashBoardActivity
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val firebaseViewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(
            this,
            firebaseViewModelProviderFactory
        ).get(FirebaseViewModel::class.java)

        binding.registerBtn.setOnClickListener {

            if (validateEmail() && validatePassword()) {
                val username = binding.emailAddress.text.toString().trim()
                val password = binding.password.text.toString().trim()

                viewModel.signUp(username, password)
            }


        }

        observeRegistration()

        binding.signupLoginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun observeRegistration() {
        viewModel.registrationStatus.observe(this, Observer { result ->
            result?.let {
                when (it) {
                    is Resource.Success -> {
                        hideProgressBar()
                        if (it.data.equals("User created!", ignoreCase = true)) {
                            //Snackbar.make(binding.root, "Registration Successfully", Snackbar.LENGTH_SHORT).show()
                            startActivity(Intent(this, CreateProfileActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Registration failed with ${it.data}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }
                    is Resource.Error -> {
                        hideProgressBar()
                        val failedMessage = it.message ?: "Unknown Error"
                        Toast.makeText(
                            this,
                            "Registration failed with $failedMessage",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    is Resource.Loading -> {
                        showProgressBar()
                    }
                }
            }

        })
    }

    private fun validateEmail(): Boolean {
        if (binding.emailAddress.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutEmail.error = "Required Field!"
            binding.emailAddress.requestFocus()
            return false
        } else {
            binding.txtInputLayoutEmail.isErrorEnabled = false
        }
        return true
    }


    /**
     * 1) field must not be empty
     * 2) password lenght must not be less than 8
     * 3) password must contain at least one digit
     * 4) password must contain atleast one upper and one lower case letter
     * 5) password must contain atleast one special character.
     */
    private fun validatePassword(): Boolean {
        if (binding.password.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutPassword.error = "Required Field!"
            binding.password.requestFocus()
            return false
        } else if (binding.password.text.toString().length < 8) {
            binding.txtInputLayoutPassword.error = "password can't be less than 8"
            binding.password.requestFocus()
            return false
        } else {
            binding.txtInputLayoutPassword.isErrorEnabled = false
        }
        return true
    }

    private fun hideProgressBar() {
        binding.progressCircular.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressCircular.visibility = View.VISIBLE
    }
}
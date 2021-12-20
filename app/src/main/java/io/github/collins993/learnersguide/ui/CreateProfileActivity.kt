package io.github.collins993.learnersguide.ui

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.databinding.ActivityCreateProfileBinding
import io.github.collins993.learnersguide.databinding.ActivityDashBoardBinding
import io.github.collins993.learnersguide.model.Users
import io.github.collins993.learnersguide.ui.dashboard.DashBoardActivity
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory
import java.io.ByteArrayOutputStream

class CreateProfileActivity : AppCompatActivity() {


    private lateinit var binding: ActivityCreateProfileBinding
    private lateinit var viewModel: FirebaseViewModel
    private val auth = FirebaseAuth.getInstance()
    private var img: Uri? = null

    companion object {
        private const val TAG = "CreateActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val firebaseViewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(
            this,
            firebaseViewModelProviderFactory
        ).get(FirebaseViewModel::class.java)

        binding.createProfileBtn.setOnClickListener {

            saveUserToFireStore()
        }

        observerSaveUserToFireStore()

        binding.profileImg.setOnClickListener {
            pickImage()
        }
    }


    private fun saveUserToFireStore() {

        if (img != null) {

            viewModel.uploadToStorage(img!!)


            viewModel.downloadUrlStatus.observe(this, Observer { result ->
                result?.let {
                    when (it) {
                        is Resource.Success -> {
                            hideProgressBar()
                            val downloadUrl = it.data
                            if (validateUserName() && validateFirstName() && validateLastName()) {
                                val username = binding.username.text.toString().trim()
                                val firstname = binding.firstname.text.toString().trim()
                                val lastname = binding.lastname.text.toString().trim()

                                val user = Users(
                                    username = username,
                                    emailAddress = auth.currentUser?.email,
                                    firstname = firstname,
                                    lastname = lastname,
                                    img = downloadUrl,
                                    uid = auth.currentUser?.uid
                                )

                                viewModel.addUserToFirestore(user)
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
        } else {
            Toast.makeText(this, "Select a picture", Toast.LENGTH_SHORT).show()

        }


    }

    private fun observerSaveUserToFireStore() {
        viewModel.addUserStatus.observe(this, Observer { result ->

            result?.let {
                when (it) {
                    is Resource.Success -> {
                        hideProgressBar()
                        if (it.data.equals("User added Successfully", ignoreCase = true)) {
                            //Snackbar.make(binding.root, "Registration Successfully", Snackbar.LENGTH_SHORT).show()
                            startActivity(Intent(this, DashBoardActivity::class.java))
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

    private fun pickImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 400)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 400 && resultCode == Activity.RESULT_OK && data?.data != null) {

            img = data.data!!
            binding.profileImg.setImageURI(data.data)

        }
    }

    /**
     * field must not be empy
     */
    private fun validateUserName(): Boolean {
        if (binding.username.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutUsername.error = "Required Field!"
            binding.username.requestFocus()
            return false
        } else {
            binding.txtInputLayoutUsername.isErrorEnabled = false
        }
        return true
    }

    /**
     * field must not be empy
     */
    private fun validateFirstName(): Boolean {
        if (binding.firstname.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutFirstname.error = "Required Field!"
            binding.firstname.requestFocus()
            return false
        } else {
            binding.txtInputLayoutFirstname.isErrorEnabled = false
        }
        return true
    }

    /**
     * field must not be empy
     */
    private fun validateLastName(): Boolean {
        if (binding.lastname.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutLastname.error = "Required Field!"
            binding.lastname.requestFocus()
            return false
        } else {
            binding.txtInputLayoutLastname.isErrorEnabled = false
        }
        return true
    }

    private fun hideProgressBar() {
        binding.progressCircular.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.progressCircular.visibility = View.VISIBLE
    }


//    private fun getImageByteArray(photoUri: Uri): ByteArray {
//        val orignalBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            val source = ImageDecoder.createSource(contentResolver, photoUri)
//            ImageDecoder.decodeBitmap(source)
//        }
//        else{
//            MediaStore.Images.Media.getBitmap(contentResolver,photoUri)
//        }
//        Log.i(TAG, "Original width ${orignalBitmap.width} and height ${orignalBitmap.height}")
//        val scaleBitmap = BitmapScaler.scaleToFitHeight(orignalBitmap, 250)
//        Log.i(TAG, "Scaled width ${scaleBitmap.width} and height ${scaleBitmap.height}")
//        val byteOutputStream = ByteArrayOutputStream()
//        scaleBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteOutputStream)
//        return byteOutputStream.toByteArray()
//
//    }
}
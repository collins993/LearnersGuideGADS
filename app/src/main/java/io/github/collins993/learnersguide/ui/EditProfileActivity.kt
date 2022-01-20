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
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import io.github.collins993.learnersguide.databinding.ActivityEditProfileBinding
import io.github.collins993.learnersguide.model.SuggestedCourses
import io.github.collins993.learnersguide.model.Users
import io.github.collins993.learnersguide.ui.dashboard.DashBoardActivity
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity() {


    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var viewModel: FirebaseViewModel
    private val auth = FirebaseAuth.getInstance()
    private var img: Uri? = null
    private val storage = Firebase.storage

    private var username: String = ""
    private var emailAddress: String = ""
    private var firstname: String = ""
    private var lastname: String = ""
    private var image: String = ""
    private var uid: String = ""

    private var updatedUsername: String = ""
    private var updatedFirstname: String = ""
    private var updatedLastname: String = ""
    private var facebookName: String = ""
    private var linkedlnName: String = ""
    private var githubName: String = ""

    companion object {
        private const val TAG = "CreateActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firebaseViewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(
            this,
            firebaseViewModelProviderFactory
        ).get(FirebaseViewModel::class.java)

        viewModel.getUserFromFirestore()

        viewModel.getAllCourseSuggestion()

        viewModel.getUsersSuggestions()

        viewModel.getUserStatus.observe(this, Observer { result ->

            result?.let {
                when (it) {
                    is Resource.Success -> {

                        val userList = it.data

                        for (user in userList!!) {
                            if (user.uid == FirebaseAuth.getInstance().currentUser?.uid) {

                                img = user.img?.toUri()

                                binding.username.setText(
                                    user.username,
                                    TextView.BufferType.EDITABLE
                                )
                                binding.firstname.setText(
                                    user.firstname,
                                    TextView.BufferType.EDITABLE
                                )
                                binding.lastname.setText(
                                    user.lastname,
                                    TextView.BufferType.EDITABLE
                                )
                                binding.facebook.setText(
                                    user.facebookName,
                                    TextView.BufferType.EDITABLE
                                )
                                binding.linkedln.setText(
                                    user.linkedlnName,
                                    TextView.BufferType.EDITABLE
                                )
                                binding.github.setText(
                                    user.githubName,
                                    TextView.BufferType.EDITABLE
                                )

                                username = user.username!!
                                firstname = user.firstname!!
                                lastname = user.lastname!!
                                image = user.img!!
                                uid = user.uid!!
                            }
                        }

                    }
                    is Resource.Error -> {
                        val failedMessage = it.message ?: "Unknown Error"
                        Toast.makeText(
                            this,
                            "Registration failed with $failedMessage",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is Resource.Loading -> {

                    }
                }
            }

        })

        binding.createProfileBtn.setOnClickListener {

            saveUserToFireStore()
            //finish()
        }

        observerUpdateUserToFireStore()

        viewModel.updateInfoUserStatus.observe(this, Observer { result ->
            result?.let {
                when (it) {
                    is Resource.Success -> {
                        hideProgressBar()
                        if (it.data.equals("Profile Info Updated", ignoreCase = true)) {
                            binding.createProfileBtn.isEnabled = true
                            finish()
                        }
                    }
                    is Resource.Error -> {
                        binding.createProfileBtn.isEnabled = true
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


    private fun saveUserToFireStore() {

        binding.createProfileBtn.isEnabled = false

        updatedUsername = binding.username.text.toString().trim()
        updatedFirstname = binding.firstname.text.toString().trim()
        updatedLastname = binding.lastname.text.toString().trim()
        facebookName = binding.facebook.text.toString().trim()
        linkedlnName = binding.linkedln.text.toString().trim()
        githubName = binding.github.text.toString().trim()

        val oldUser = Users(
            username = username,
            emailAddress = auth.currentUser?.email,
            firstname = firstname,
            lastname = lastname,
            img = image,
            uid = uid
        )

        val map = mutableMapOf<String, Any>()

        map["firstname"] = updatedFirstname
        map["lastname"] = updatedLastname
        map["username"] = updatedUsername
        map["facebookName"] = facebookName
        map["linkedlnName"] = linkedlnName
        map["githubName"] = githubName

        viewModel.updateUserInfo(oldUser, map)


    }

    private fun observerUpdateUserToFireStore() {

        viewModel.updateUserStatus.observe(this, Observer { result ->

            result?.let {
                when (it) {
                    is Resource.Success -> {
                        hideProgressBar()
                        if (it.data.equals("Profile Updated", ignoreCase = true)) {

                            viewModel.getAllSuggestionStatus.observe(this, Observer { result ->

                                result?.let {
                                    when (it) {
                                        is Resource.Success -> {
                                            val userList = it.data

                                            for (user in userList!!) {
                                                if (user.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                                                    //do stuff

                                                    val oldSuggestionInfo =
                                                        SuggestedCourses(
                                                            title = user.title,
                                                            url = user.url,
                                                            username = user.username,
                                                            emailAddress = user.emailAddress,
                                                            firstname = user.firstname,
                                                            lastname = user.lastname,
                                                            img = user.img,
                                                            uid = user.uid,
                                                            date = user.date,

                                                            )

                                                    val map =
                                                        mutableMapOf<String, Any>()
                                                    map["firstname"] = updatedFirstname
                                                    map["lastname"] = updatedLastname
                                                    map["username"] = updatedUsername
                                                    map["facebookName"] = facebookName
                                                    map["linkedlnName"] = linkedlnName
                                                    map["githubName"] = githubName

                                                    viewModel.updateSuggestionProfileInfo(
                                                        oldSuggestionInfo,
                                                        map
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }


                            })

                        } else {
                            hideProgressBar()
                            binding.createProfileBtn.isEnabled = true
                            Toast.makeText(
                                this,
                                "Registration failed with ${it.data}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.createProfileBtn.isEnabled = true
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

    private fun validateFaceBookName(): Boolean {
        if (binding.facebook.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutFacebook.error = "Required Field!"
            binding.facebook.requestFocus()
            return false
        } else {
            binding.txtInputLayoutFacebook.isErrorEnabled = false
        }
        return true
    }

    private fun validateLinkedlnName(): Boolean {
        if (binding.linkedln.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutLinkedln.error = "Required Field!"
            binding.facebook.requestFocus()
            return false
        } else {
            binding.txtInputLayoutLinkedln.isErrorEnabled = false
        }
        return true
    }

    private fun validateGithubName(): Boolean {
        if (binding.github.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutGithub.error = "Required Field!"
            binding.facebook.requestFocus()
            return false
        } else {
            binding.txtInputLayoutGithub.isErrorEnabled = false
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
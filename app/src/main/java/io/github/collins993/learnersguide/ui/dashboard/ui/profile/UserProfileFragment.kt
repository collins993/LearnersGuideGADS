package io.github.collins993.learnersguide.ui.dashboard.ui.profile

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.adapter.SuggestedCourseAdapter
import io.github.collins993.learnersguide.databinding.FragmentHomeBinding
import io.github.collins993.learnersguide.databinding.FragmentUserProfileBinding
import io.github.collins993.learnersguide.db.CourseDatabase
import io.github.collins993.learnersguide.model.SuggestedCourses
import io.github.collins993.learnersguide.model.Users
import io.github.collins993.learnersguide.repository.Repository
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory
import io.github.collins993.learnersguide.viewmodel.MyViewModel
import io.github.collins993.learnersguide.viewmodel.ViewModelProviderFactory


class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {


    private lateinit var viewModel: FirebaseViewModel
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var suggestedCourseAdapter: SuggestedCourseAdapter
    private var img: Uri? = null

    private var username: String = ""
    private var emailAddress: String = ""
    private var firstname: String = ""
    private var lastname: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var title: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding = FragmentUserProfileBinding.bind(view)
        val firebaseViewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(
            this,
            firebaseViewModelProviderFactory
        ).get(FirebaseViewModel::class.java)


        viewModel.getUserFromFirestore()

        viewModel.getUsersSuggestions()

        viewModel.getAllCourseSuggestion()

        setUpRecyclerView()

        viewModel.getUserStatus.observe(viewLifecycleOwner, Observer { result ->

            result?.let {
                when (it) {

                    is Resource.Success -> {
                        val userList = it.data

                        for (user in userList!!) {
                            if (user.uid == FirebaseAuth.getInstance().currentUser?.uid) {
                                Glide.with(this)
                                    .load(user.img)
                                    .into(binding.profileImg)

                                binding.username.text = user.username
                                binding.firstname.text = user.firstname
                                binding.lastname.text = user.lastname
                                binding.emailAddress.text = user.emailAddress
                                binding.facebookIcon.text = user.facebookName
                                binding.linkedln.text = user.linkedlnName
                                binding.github.text = user.githubName

                            }
                        }


                    }
                    is Resource.Error -> {

                        val failedMessage = it.message ?: "Unknown Error"
                        Toast.makeText(
                            activity,
                            "Registration failed with $failedMessage",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    is Resource.Loading -> {

                    }
                }
            }

        })

        viewModel.getUserSuggestionStatus.observe(viewLifecycleOwner, Observer { result ->

            result?.let {
                when (it) {
                    is Resource.Success -> {

                        val usersSuggestions = it.data

                        suggestedCourseAdapter.differ.submitList(usersSuggestions)

                        for (suggestion in usersSuggestions!!) {

                            if (suggestion.uid == FirebaseAuth.getInstance().currentUser?.uid) {

                                firstname = suggestion.firstname.toString()
                                emailAddress = suggestion.emailAddress.toString()
                                uid = suggestion.uid.toString()

                            }


                        }


                    }
                }
            }

        })

        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_editProfileActivity)
        }

        binding.editProfilePic.setOnClickListener {
            pickImage()
        }

        suggestedCourseAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("suggestedCourses", it)
            }
            findNavController().navigate(
                R.id.action_nav_profile_to_suggestedWebViewFragment,
                bundle
            )
        }

        //function to Swipe to delete article from database
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val suggestedCourse = suggestedCourseAdapter.differ.currentList[position]

                viewModel.deleteSuggestion(suggestedCourse)
                Snackbar.make(binding.root, "Successfully Deleted Course", Snackbar.LENGTH_LONG)
                    .apply {
                        show()
                    }


            }

        }

        //Attach to recyclerview
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvUsersSuggestions)
        }

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

            viewModel.uploadToStorage(img!!)
            binding.profileImg.setImageURI(data.data)


            viewModel.downloadUrlStatus.observe(viewLifecycleOwner, Observer { result ->

                result?.let {
                    when (it) {
                        is Resource.Success -> {
                            hideProgressBar()
                            val imageString = it.data

                            val updatedUser = Users(
                                uid = uid
                            )

                            val map =
                                mutableMapOf<String, Any>()
                            map["img"] =
                                imageString.toString()

                            viewModel.updateUserInfo(updatedUser, map)
                        }
                        is Resource.Error -> {
                            hideProgressBar()
                        }
                        is Resource.Loading -> {
                            showProgressBar()
                        }
                    }
                }

            })

            viewModel.updateUserStatus.observe(viewLifecycleOwner, Observer { result ->

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

                                                        viewModel.downloadUrlStatus.observe(
                                                            this,
                                                            Observer { result ->
                                                                result?.let {
                                                                    when (it) {
                                                                        is Resource.Success -> {
                                                                            hideProgressBar()
                                                                            val downloadUrl =
                                                                                it.data

//                                                                        updatedUsername = binding.username.text.toString().trim()
//                                                                        updatedFirstname = binding.firstname.text.toString().trim()
//                                                                        updatedLastname = binding.lastname.text.toString().trim()

                                                                            val oldSuggestionInfo =
                                                                                SuggestedCourses(
                                                                                    uid = user.uid,
                                                                                )

                                                                            val map =
                                                                                mutableMapOf<String, Any>()
                                                                            map["img"] =
                                                                                downloadUrl.toString()

                                                                            viewModel.updateSuggestionProfileInfo(
                                                                                oldSuggestionInfo,
                                                                                map
                                                                            )


                                                                            //finish()

                                                                        }
                                                                        is Resource.Error -> {
                                                                            hideProgressBar()
                                                                            val failedMessage =
                                                                                it.message
                                                                                    ?: "Unknown Error"
                                                                            Toast.makeText(
                                                                                activity,
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
                                                }
                                            }
                                        }
                                    }


                                })

                            } else {
                                Toast.makeText(
                                    activity,
                                    "Registration failed with ${it.data}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        is Resource.Error -> {
                            hideProgressBar()
                            val failedMessage = it.message ?: "Unknown Error"
                            Toast.makeText(
                                activity,
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
    }

    private fun setUpRecyclerView() {
        suggestedCourseAdapter = SuggestedCourseAdapter()
        binding.rvUsersSuggestions.apply {
            adapter = suggestedCourseAdapter
            layoutManager = LinearLayoutManager(activity)
            //addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun hideProgressBar() {
        binding.pickImgProgress.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding.pickImgProgress.visibility = View.VISIBLE
    }
}
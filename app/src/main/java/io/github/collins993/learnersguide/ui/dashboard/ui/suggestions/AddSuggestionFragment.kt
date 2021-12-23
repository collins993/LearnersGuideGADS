package io.github.collins993.learnersguide.ui.dashboard.ui.suggestions

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.databinding.FragmentAddSuggestionBinding
import io.github.collins993.learnersguide.databinding.FragmentUserProfileBinding
import io.github.collins993.learnersguide.model.SuggestedCourses
import io.github.collins993.learnersguide.ui.dashboard.DashBoardActivity
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory

class AddSuggestionFragment : Fragment(R.layout.fragment_add_suggestion) {

    private lateinit var viewModel: FirebaseViewModel
    private lateinit var binding: FragmentAddSuggestionBinding
    private var suggestedCourses: SuggestedCourses? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddSuggestionBinding.bind(view)
        val firebaseViewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(
            this,
            firebaseViewModelProviderFactory
        ).get(FirebaseViewModel::class.java)

        viewModel.getUserFromFirestore()


        binding.addSuggestionBtn.setOnClickListener {

            viewModel.getUserStatus.observe(viewLifecycleOwner, Observer { result ->

                result?.let {
                    when (it) {
                        is Resource.Success -> {
                            hideProgressBar()

                            val userList = it.data
                            for (user in  userList!!){
                                if (user.uid == FirebaseAuth.getInstance().currentUser?.uid){

                                    if (validateTitle() && validateUrl()){
                                        val title = binding.courseTitle.text.toString().trim()
                                        val url = binding.courseUrl.text.toString().trim()

                                        suggestedCourses = SuggestedCourses(
                                            title = title,
                                            url =  url,
                                            username = user.username,
                                            emailAddress = user.emailAddress,
                                            firstname = user.firstname,
                                            lastname = user.lastname,
                                            img = user.img,
                                            uid = user.uid,
                                            date = System.currentTimeMillis()
                                        )


                                    }

                                }
                            }
                            viewModel.addSuggestion(suggestedCourses!!)

                        }
                        is Resource.Error -> {

                            hideProgressBar()
                            val failedMessage =  it.message ?: "Unknown Error"
                            Toast.makeText(activity,"Registration failed with $failedMessage", Toast.LENGTH_LONG).show()

                        }
                        is Resource.Loading -> {

                            showProgressBar()
                        }
                    }
                }

            })
        }


        viewModel.addSuggestionStatus.observe(viewLifecycleOwner, Observer { result ->
            result?.let{
                when(it){
                    is Resource.Success -> {
                        if (it.data.equals("Course Suggested", ignoreCase = true)){
                            findNavController().navigate(R.id.action_addSuggestionFragment_to_nav_suggestion)
                        }
                        else{
                            Toast.makeText(activity, "Registration failed with ${it.data}", Toast.LENGTH_LONG).show()
                        }
                    }
                    is Resource.Error -> {

                        val failedMessage =  it.message ?: "Unknown Error"
                        Toast.makeText(activity,"Registration failed with $failedMessage", Toast.LENGTH_LONG).show()

                    }
                    is Resource.Loading -> {

                    }
                }
            }

        })

    }

    /**
     * field must not be empy
     */
    private fun validateTitle(): Boolean {
        if (binding.courseTitle.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutTitle.error = "Required Field!"
            binding.courseTitle.requestFocus()
            return false
        } else {
            binding.txtInputLayoutTitle.isErrorEnabled = false
        }
        return true
    }

    /**
     * field must not be empy
     */
    private fun validateUrl(): Boolean {
        if (binding.courseUrl.text.toString().trim().isEmpty()) {
            binding.txtInputLayoutUrl.error = "Required Field!"
            binding.courseUrl.requestFocus()
            return false
        } else {
            binding.txtInputLayoutUrl.isErrorEnabled = false
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
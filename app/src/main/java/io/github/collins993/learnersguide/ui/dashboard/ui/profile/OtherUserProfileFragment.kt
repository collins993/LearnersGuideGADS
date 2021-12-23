package io.github.collins993.learnersguide.ui.dashboard.ui.profile

import android.app.Application
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.adapter.SuggestedCourseAdapter
import io.github.collins993.learnersguide.databinding.FragmentHomeBinding
import io.github.collins993.learnersguide.databinding.FragmentOtherUserProfileBinding
import io.github.collins993.learnersguide.databinding.FragmentUserProfileBinding
import io.github.collins993.learnersguide.db.CourseDatabase
import io.github.collins993.learnersguide.model.SuggestedCourses
import io.github.collins993.learnersguide.repository.Repository
import io.github.collins993.learnersguide.ui.dashboard.ui.home.WebViewFragmentArgs
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory
import io.github.collins993.learnersguide.viewmodel.MyViewModel
import io.github.collins993.learnersguide.viewmodel.ViewModelProviderFactory


class OtherUserProfileFragment : Fragment(R.layout.fragment_other_user_profile) {


    private lateinit var viewModel: FirebaseViewModel
    private lateinit var binding: FragmentOtherUserProfileBinding
    private lateinit var suggestedCourseAdapter: SuggestedCourseAdapter
    val args: OtherUserProfileFragmentArgs by navArgs()
    private var arrayList = ArrayList<SuggestedCourses>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding = FragmentOtherUserProfileBinding.bind(view)
        val firebaseViewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(
            this,
            firebaseViewModelProviderFactory
        ).get(FirebaseViewModel::class.java)

        arrayList = ArrayList()

        val user = args.otherUser

        viewModel.getUserFromFirestore()

        viewModel.getAllCourseSuggestion()

        setUpRecyclerView()



        Glide.with(this)
            .load(user.img)
            .into(binding.profileImg)

        binding.username.text = user.username
        binding.firstname.text = user.firstname
        binding.lastname.text = user.lastname
        binding.emailAddress.text = user.emailAddress


        viewModel.getAllSuggestionStatus.observe(viewLifecycleOwner, Observer { result ->

            result?.let {
                when (it) {
                    is Resource.Success -> {

                        val listOfSuggestedCourses = it.data
                        for (course in listOfSuggestedCourses!!){
                            if (course.uid == user.uid){
                                arrayList.add(course)

                            }
                        }
                        suggestedCourseAdapter.differ.submitList(arrayList)


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


        suggestedCourseAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("suggestedCourses", it)
            }
            findNavController().navigate(
                R.id.action_otherUserProfileFragment_to_suggestedWebViewFragment,
                bundle
            )
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
}
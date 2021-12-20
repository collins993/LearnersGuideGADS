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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.adapter.SuggestedCourseAdapter
import io.github.collins993.learnersguide.databinding.FragmentHomeBinding
import io.github.collins993.learnersguide.databinding.FragmentUserProfileBinding
import io.github.collins993.learnersguide.db.CourseDatabase
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

        setUpRecyclerView()

        viewModel.getUserStatus.observe(viewLifecycleOwner, Observer { result ->

            result?.let {
                when (it) {

                    is Resource.Success -> {
                        val user = it.data
                        Log.i("Users", user.toString())

                        Glide.with(this)
                            .load(user?.img)
                            .into(binding.profileImg)

                        binding.username.text = user?.username
                        binding.firstname.text = user?.firstname
                        binding.lastname.text = user?.lastname
                        binding.emailAddress.text = user?.emailAddress
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
                        suggestedCourseAdapter.differ.submitList(it.data)
                    }
                }
            }


        })

        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_nav_profile_to_editProfileActivity)
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
                        setAction("Undo") {
                            viewModel.addSuggestion(suggestedCourse)
                        }
                        show()
                    }
            }

        }

        //Attach to recyclerview
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvUsersSuggestions)
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
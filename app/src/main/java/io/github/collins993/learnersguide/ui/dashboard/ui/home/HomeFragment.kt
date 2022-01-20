package io.github.collins993.learnersguide.ui.dashboard.ui.home

import android.app.Application
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.adapter.CourseAdapter
import io.github.collins993.learnersguide.databinding.FragmentHomeBinding
import io.github.collins993.learnersguide.repository.Repository
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.MyViewModel
import io.github.collins993.learnersguide.viewmodel.ViewModelProviderFactory
import io.github.collins993.learnersguide.db.CourseDatabase

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var homeViewModel: MyViewModel
    private lateinit var binding: FragmentHomeBinding
    private lateinit var courseAdapter: CourseAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)
        val courseRepository = Repository(CourseDatabase(requireActivity().applicationContext))
        val viewModelProviderFactory = ViewModelProviderFactory(Application(), courseRepository)
        homeViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MyViewModel::class.java)

        setUpRecyclerView()

        courseAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("courses", it)
            }
            findNavController().navigate(R.id.action_nav_home_to_webViewFragment, bundle)
        }

        homeViewModel.courseList.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.results?.let { results ->
                        courseAdapter.differ.submitList(results.map { it.toCourses() })

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    Toast.makeText(activity, "${response.message}", Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }

        })
    }

    private fun setUpRecyclerView() {
        courseAdapter = CourseAdapter()
        binding.rvCourses.apply {
            adapter = courseAdapter
            layoutManager = LinearLayoutManager(activity)
            //addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE

    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE

    }
}
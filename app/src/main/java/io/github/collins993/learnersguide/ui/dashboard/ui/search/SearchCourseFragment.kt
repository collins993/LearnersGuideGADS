package io.github.collins993.learnersguide.ui.dashboard.ui.search

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.adapter.CourseAdapter
import io.github.collins993.learnersguide.databinding.FragmentHomeBinding
import io.github.collins993.learnersguide.databinding.SearchCourseFragmentBinding
import io.github.collins993.learnersguide.db.CourseDatabase
import io.github.collins993.learnersguide.repository.Repository
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.MyViewModel
import io.github.collins993.learnersguide.viewmodel.ViewModelProviderFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchCourseFragment : Fragment(R.layout.search_course_fragment) {

    private lateinit var viewModel: MyViewModel
    private lateinit var binding: SearchCourseFragmentBinding
    lateinit var courseAdapter: CourseAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = SearchCourseFragmentBinding.bind(view)
        val courseRepository = Repository(CourseDatabase(requireActivity().applicationContext))
        val viewModelProviderFactory = ViewModelProviderFactory(Application(), courseRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MyViewModel::class.java)

        setUpRecyclerView()

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(500L)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchCourse(editable.toString())
                    }
                }
            }

        }

        viewModel.searchCourse.observe(viewLifecycleOwner, Observer { response ->
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
        binding.rvSearchCourse.apply {
            adapter = courseAdapter
            layoutManager = LinearLayoutManager(activity)
            //addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }


    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        //isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        //isLoading = true
    }

}
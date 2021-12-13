package io.github.collins993.learnersguide.ui.dashboard.ui.favourites

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.adapter.CourseAdapter
import io.github.collins993.learnersguide.databinding.FragmentFavouritesBinding
import io.github.collins993.learnersguide.repository.Repository
import io.github.collins993.learnersguide.viewmodel.MyViewModel
import io.github.collins993.learnersguide.viewmodel.ViewModelProviderFactory
import io.github.collins993.learnersguide.db.CourseDatabase

class FavouritesFragment : Fragment() {

    private lateinit var favouritesViewModel: MyViewModel
    private var _binding: FragmentFavouritesBinding? = null


    lateinit var courseAdapter: CourseAdapter
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val courseRepository = Repository(CourseDatabase(requireActivity().applicationContext))
        val viewModelProviderFactory = ViewModelProviderFactory(Application(), courseRepository)
        favouritesViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MyViewModel::class.java)


        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setUpRecyclerView()

        favouritesViewModel.getSavedCourse().observe(viewLifecycleOwner, Observer {

            courseAdapter.differ.submitList(it)

        })

        courseAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("courses", it)
            }
            findNavController().navigate(R.id.action_nav_favourites_to_webViewFragment, bundle)
        }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        courseAdapter = CourseAdapter()
        binding.rvSavedCourse.apply {
            adapter = courseAdapter
            layoutManager = LinearLayoutManager(activity)
            //addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}
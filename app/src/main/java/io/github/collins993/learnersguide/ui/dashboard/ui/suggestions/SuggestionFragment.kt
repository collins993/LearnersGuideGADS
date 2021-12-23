package io.github.collins993.learnersguide.ui.dashboard.ui.suggestions

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.adapter.CourseAdapter
import io.github.collins993.learnersguide.adapter.SuggestedCourseAdapter
import io.github.collins993.learnersguide.databinding.FragmentSuggestionBinding
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory

class SuggestionFragment : Fragment() {

    private var _binding: FragmentSuggestionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: FirebaseViewModel
    lateinit var suggestdCourseAdapter: SuggestedCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val firebaseViewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(
            this,
            firebaseViewModelProviderFactory
        ).get(FirebaseViewModel::class.java)

        _binding = FragmentSuggestionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setUpRecyclerView()
        viewModel.getAllCourseSuggestion()

        binding.addSuggestedCourse.setOnClickListener {

            findNavController().navigate(R.id.action_nav_suggestion_to_addSuggestionFragment)
        }

        viewModel.getAllSuggestionStatus.observe(viewLifecycleOwner, Observer { result ->

            result?.let {
                when(it){
                   is Resource.Success -> {
                       suggestdCourseAdapter.differ.submitList(it.data)
                    }
                }
            }


        })

        suggestdCourseAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("suggestedCourses", it)
            }
            findNavController().navigate(R.id.action_nav_suggestion_to_suggestedWebViewFragment, bundle)
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
                val suggestedCourse = suggestdCourseAdapter.differ.currentList[position]


                val action = SuggestionFragmentDirections.actionNavSuggestionToOtherUserProfileFragment(suggestedCourse)
                findNavController().navigate(action)

            }

        }

        //Attach to recyclerview
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvAllSuggestions)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        suggestdCourseAdapter = SuggestedCourseAdapter()
        binding.rvAllSuggestions.apply {
            adapter = suggestdCourseAdapter
            layoutManager = LinearLayoutManager(activity)
            //addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}
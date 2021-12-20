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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
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

        //function to Swipe to delete article from database
        val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val course = courseAdapter.differ.currentList[position]

                favouritesViewModel.deleteCourse(course)
                Snackbar.make(binding.root, "Successfully Deleted Course", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        favouritesViewModel.saveCourse(course)
                    }
                    show()
                }
            }

        }

        //Attach to recyclerview
        ItemTouchHelper(itemTouchHelperCallBack).apply {
            attachToRecyclerView(binding.rvSavedCourse)
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
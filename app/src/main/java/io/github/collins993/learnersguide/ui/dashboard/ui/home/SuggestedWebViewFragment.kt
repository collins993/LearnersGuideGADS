package io.github.collins993.learnersguide.ui.dashboard.ui.home

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import io.github.collins993.learnersguide.databinding.FragmentSuggestedWebViewBinding
import io.github.collins993.learnersguide.databinding.FragmentWebViewBinding
import io.github.collins993.learnersguide.db.entity.Courses
import io.github.collins993.learnersguide.repository.Repository
import io.github.collins993.learnersguide.utils.Constants.Companion.BASE_URL
import io.github.collins993.learnersguide.viewmodel.MyViewModel
import io.github.collins993.learnersguide.viewmodel.ViewModelProviderFactory
import io.github.collins993.learnersguide.db.CourseDatabase

class SuggestedWebViewFragment : Fragment() {

    private var _binding: FragmentSuggestedWebViewBinding? = null

    private val binding get() = _binding!!

    val args: SuggestedWebViewFragmentArgs by navArgs()
    private lateinit var webViewViewModel: MyViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentSuggestedWebViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val courseRepository = Repository(CourseDatabase(requireActivity().applicationContext))
        val viewModelProviderFactory = ViewModelProviderFactory(Application(), courseRepository)
        webViewViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MyViewModel::class.java)


        val courses = args.suggestedCourses
        //val suggestedCourses = args.suggestedCourses


        binding.webView.apply {
            webViewClient = WebViewClient()
            courses.url?.let { loadUrl(it) }

//            suggestedCourses.url?.let { loadUrl(it) }
        }

//        binding.fab.setOnClickListener {
//
//            val saveCourse = Courses(
//                title = courses.title,
//                headLine = courses.username,
//                url = courses.url,
//                image = courses.img
//            )
//            webViewViewModel.saveCourse(saveCourse)
//            Snackbar.make(binding.root, "Course saved successfully", Snackbar.LENGTH_SHORT).show()
//        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
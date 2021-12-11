package io.github.collins993.learnersguide.ui.dashboard.ui.home

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.github.collins993.learnersguide.databinding.FragmentHomeBinding
import io.github.collins993.learnersguide.repository.Repository
import io.github.collins993.learnersguide.utils.Constants.Companion.BASE_URL
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.MyViewModel
import io.github.collins993.learnersguide.viewmodel.ViewModelProviderFactory

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: MyViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //val newsRepository = NewsRepository(ArticleDatabase(requireActivity().applicationContext))
        val viewModelProviderFactory = ViewModelProviderFactory(Application(), Repository())
        homeViewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(MyViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.courseList.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is Resource.Success -> {
                    //hideProgressBar()
                    response.data?.results?.let { newsResponse ->

                        for (singleResult in newsResponse) {
                            singleResult.title
                            singleResult.headline
                            singleResult.price
                            singleResult.url

                            Log.i("responseUrl", "${BASE_URL + singleResult.url}")
                            Log.i("image", "${singleResult.image480x270}")

                            Log.i(
                                "response",
                                "${singleResult.title} \n" +
                                        " ${singleResult.headline} \n" +
                                        " ${singleResult.price} \n" +
                                        " ${singleResult.url} "
                            )
                        }


//                        newsAdapter.differ.submitList(newsResponse.articles.toList())
//                        val totalPages = newsResponse.totalResults / QUERY_PAGE_SIZE + 2
//                        isLastPage = viewModel.breakingNewsPage == totalPages
//                        if(isLastPage) {
//                            binding.rvBreakingNews.setPadding(0,0,0,0)
//                        }
                    }
                }
            }

        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
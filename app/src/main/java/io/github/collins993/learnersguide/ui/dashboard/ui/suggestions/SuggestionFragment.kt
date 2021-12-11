package io.github.collins993.learnersguide.ui.dashboard.ui.suggestions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.github.collins993.learnersguide.databinding.FragmentSuggestionBinding

class SuggestionFragment : Fragment() {

    private lateinit var suggestionViewModel: SuggestionViewModel
    private var _binding: FragmentSuggestionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        suggestionViewModel =
            ViewModelProvider(this).get(SuggestionViewModel::class.java)

        _binding = FragmentSuggestionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textSlideshow
        suggestionViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
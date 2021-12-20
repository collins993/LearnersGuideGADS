package io.github.collins993.learnersguide.ui.dashboard.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.databinding.FragmentAboutAppBinding
import io.github.collins993.learnersguide.databinding.FragmentHomeBinding


class AboutAppFragment : Fragment(R.layout.fragment_about_app) {

    private lateinit var binding: FragmentAboutAppBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAboutAppBinding.bind(view)
    }
}
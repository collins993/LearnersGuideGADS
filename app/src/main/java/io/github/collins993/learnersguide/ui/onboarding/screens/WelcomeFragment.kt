package io.github.collins993.learnersguide.ui.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.databinding.ActivityMainBinding
import io.github.collins993.learnersguide.databinding.FragmentHomeBinding
import io.github.collins993.learnersguide.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    private lateinit var binding: FragmentWelcomeBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWelcomeBinding.bind(view)

        val viewPager2 = activity?.findViewById<ViewPager2>(R.id.view_pager_2)

        binding.next.setOnClickListener {
            viewPager2?.currentItem = 1
        }

    }


}
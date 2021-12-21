
package io.github.collins993.learnersguide.ui.onboarding.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.adapter.ViewPagerAdapter
import io.github.collins993.learnersguide.databinding.ActivityViewPagerBinding
import io.github.collins993.learnersguide.ui.authentication.SignUpActivity
import io.github.collins993.learnersguide.utils.UserManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ViewPagerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewPagerBinding
    private lateinit var userManager: UserManager
    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        userManager = UserManager(this)

        val fragmentsList = arrayListOf<Fragment>(
            WelcomeFragment(),
            SuggestScreenFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentsList,
            supportFragmentManager,
            lifecycle
        )

        binding.viewPager2.adapter = adapter


        val doppelgangerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //Toast.makeText(applicationContext, "Selected position: ${position}",Toast.LENGTH_SHORT).show()
                if (position == 1){
                    binding.button.visibility = View.VISIBLE
                }
                else{
                    binding.button.visibility = View.GONE
                }
            }
        }

        binding.viewPager2.registerOnPageChangeCallback(doppelgangerPageChangeCallback)

        binding.button.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            GlobalScope.launch {
                userManager.onboardingFinished(true)
            }
            finish()
        }

    }




}
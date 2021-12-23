package io.github.collins993.learnersguide.ui.dashboard

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.databinding.ActivityDashBoardBinding
import io.github.collins993.learnersguide.databinding.NavHeaderDashBoardBinding
import io.github.collins993.learnersguide.ui.authentication.LoginActivity
import io.github.collins993.learnersguide.utils.Resource
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory

class DashBoardActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityDashBoardBinding

    private lateinit var viewModel: FirebaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val firebaseViewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(
            this,
            firebaseViewModelProviderFactory
        ).get(FirebaseViewModel::class.java)


        setSupportActionBar(binding.appBarDashBoard.toolbar)

        viewModel.getUserFromFirestore()

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_dash_board)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_profile,
                R.id.nav_favourites,
                R.id.nav_suggestion,
                R.id.nav_search,
                R.id.profileSettingsFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        viewModel.getUserStatus.observe(this, Observer { result ->

            result?.let {
                when (it) {

                    is Resource.Success -> {
                        val userList = it.data

                        val header = navView.getHeaderView(0)
                        val username = header.findViewById<View>(R.id.username) as TextView
                        val emailAddress = header.findViewById<View>(R.id.email_address) as TextView
                        val img = header.findViewById<View>(R.id.imageView) as ImageView

                        for (user in  userList!!){
                            if (user.uid == FirebaseAuth.getInstance().currentUser?.uid){
                                username.text = user?.username
                                emailAddress.text = user?.emailAddress

                                Glide.with(this)
                                    .load(user?.img)
                                    .into(img)
                            }
                        }




                    }
                    is Resource.Error -> {

                        val failedMessage = it.message ?: "Unknown Error"
                        Toast.makeText(
                            this,
                            "Registration failed with $failedMessage",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                    is Resource.Loading -> {

                    }
                }
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dash_board, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                viewModel.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)


    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_dash_board)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
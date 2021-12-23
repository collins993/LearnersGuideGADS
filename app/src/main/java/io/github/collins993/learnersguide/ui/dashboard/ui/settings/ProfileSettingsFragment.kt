package io.github.collins993.learnersguide.ui.dashboard.ui.settings

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import io.github.collins993.learnersguide.R
import io.github.collins993.learnersguide.ui.authentication.LoginActivity
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModel
import io.github.collins993.learnersguide.viewmodel.FirebaseViewModelProviderFactory

class ProfileSettingsFragment: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var viewModel: FirebaseViewModel


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val viewModelProviderFactory = FirebaseViewModelProviderFactory(Application())
        viewModel = ViewModelProvider(requireActivity(), viewModelProviderFactory).get(FirebaseViewModel::class.java)

        viewModel.getUserFromFirestore()



        val resetPassword = findPreference<Preference>("reset_password_key")
        val editTxtUsername = findPreference<Preference>("edit_username_key")
        val aboutApp = findPreference<Preference>("about_app_key")
        val privacyPolicy = findPreference<Preference>("privacy_policy_key")
        val logOut = findPreference<Preference>("logout_key")


        viewModel.getUserStatus.observe(this, {

            val userList = it.data

            for (user in  userList!!){
                if (user.uid == FirebaseAuth.getInstance().currentUser?.uid){
                    editTxtUsername?.summary = user.username
                    editTxtUsername?.title = user.emailAddress
                }
            }



        })



        resetPassword?.setOnPreferenceClickListener {

            findNavController().navigate(R.id.action_profileSettingsFragment_to_resetPasswordFragment)
            true
        }
        aboutApp?.setOnPreferenceClickListener {

            findNavController().navigate(R.id.action_profileSettingsFragment_to_aboutAppFragment)
            true
        }
        privacyPolicy?.setOnPreferenceClickListener {
            //Open web page
            true
        }
        logOut?.setOnPreferenceClickListener {
            viewModel.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            requireActivity().finish()
            true

        }

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {

        if (key == "edit_username_key"){
            val newUsername = sharedPreferences.getString(key, "")
            Log.i("New Name", newUsername.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(context).registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(context).unregisterOnSharedPreferenceChangeListener(this)
    }

}
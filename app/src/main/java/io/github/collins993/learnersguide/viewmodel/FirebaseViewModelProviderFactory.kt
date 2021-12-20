package io.github.collins993.learnersguide.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.collins993.learnersguide.repository.Repository

class FirebaseViewModelProviderFactory(
    val app: Application

): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FirebaseViewModel(app) as T
    }
}
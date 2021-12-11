package io.github.collins993.learnersguide.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.collins993.learnersguide.repository.Repository

class ViewModelProviderFactory(
    val app: Application,
    val repository: Repository

): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MyViewModel(app, repository) as T
    }
}
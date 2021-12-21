package io.github.collins993.learnersguide.utils

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserManager(
    context: Context
) {

    private val dataStore: DataStore<Preferences> = context.createDataStore("user_prefs")

    companion object{
        val USER_NAME_KEY = preferencesKey<String>("USER_NAME")
        val USER_EMAIL_KEY = preferencesKey<String>("USER_EMAIL")
        val USER_COUNTRY_KEY = preferencesKey<String>("USER_COUNTRY")
        val USER_IS_FINISH_ONBOARDING_KEY = preferencesKey<Boolean>("ONBOARDING_FINISH")
    }

    suspend fun storeUser(username: String, email: String, country: String){
        dataStore.edit {
            it[USER_NAME_KEY] = username
            it[USER_EMAIL_KEY] = email
            it[USER_COUNTRY_KEY] = country
        }
    }
    suspend fun onboardingFinished(isFinished: Boolean){
        dataStore.edit {
            it[USER_IS_FINISH_ONBOARDING_KEY] = isFinished
        }
    }
    val usernameFlow: Flow<String> = dataStore.data.map {
        it[USER_NAME_KEY] ?: ""
    }
    val emailFlow: Flow<String> = dataStore.data.map {
        it[USER_EMAIL_KEY] ?: ""
    }
    val countryFlow: Flow<String> = dataStore.data.map {
        it[USER_COUNTRY_KEY] ?: ""
    }
    val isFinishedFlow: Flow<Boolean> = dataStore.data.map {
        it[USER_IS_FINISH_ONBOARDING_KEY] ?: false
    }
}
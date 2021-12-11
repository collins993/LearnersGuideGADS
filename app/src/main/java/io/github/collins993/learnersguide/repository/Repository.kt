package io.github.collins993.learnersguide.repository

import io.github.collins993.learnersguide.api.RetrofitInstance

class Repository {

    suspend fun getCourseList() = RetrofitInstance.api.getCourseList()

}
package io.github.collins993.learnersguide.api

import io.github.collins993.learnersguide.model.CourseResponse
import retrofit2.Response
import retrofit2.http.GET

interface UdemyApi {

    @GET("/api-2.0/courses/")
    suspend fun getCourseList(): Response<CourseResponse>
}
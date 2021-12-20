package io.github.collins993.learnersguide.api

import io.github.collins993.learnersguide.api.api_model.CourseResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UdemyApi {

    @GET("/api-2.0/courses/")
    suspend fun getCourseList(): Response<CourseResponse>

    @GET("/api-2.0/courses/")
    suspend fun searchCourse(
        @Query("search")
        searchQuery: String
    ): Response<CourseResponse>
}
package io.github.collins993.learnersguide.repository

import io.github.collins993.learnersguide.api.RetrofitInstance
import io.github.collins993.learnersguide.db.entity.Courses
import io.github.collins993.learnersguide.db.CourseDatabase

class Repository(val db : CourseDatabase) {

    suspend fun getCourseList() = RetrofitInstance.api.getCourseList()

    suspend fun upsert(courses: Courses) = db.getCourseDao().upsert(courses)

    fun getSavedCourse() = db.getCourseDao().getAllCourses()

    suspend fun deleteCourse(courses: Courses) = db.getCourseDao().deleteCourse(courses)

}
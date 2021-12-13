package io.github.collins993.learnersguide.db

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.collins993.learnersguide.db.entity.Courses

@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(course: Courses): Long

    @Query("SELECT * FROM courses_table")
    fun getAllCourses(): LiveData<List<Courses>>

    @Delete
    suspend fun deleteCourse(course: Courses)
}
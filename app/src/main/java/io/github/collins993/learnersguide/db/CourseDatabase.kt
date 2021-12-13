package io.github.collins993.learnersguide.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.collins993.learnersguide.db.entity.Courses

@Database(
        entities = [Courses::class],
    version = 1
)
abstract class CourseDatabase : RoomDatabase() {

    abstract fun getCourseDao(): CourseDao

    companion object {
        @Volatile
        private var instance: CourseDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it}
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CourseDatabase::class.java,
                "course_db.db"
            ).fallbackToDestructiveMigration().build()
    }
}
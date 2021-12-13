package io.github.collins993.learnersguide.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "courses_table")
data class Courses(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String?,
    val headLine: String?,
    val url: String?,
    val image: String?
): Serializable
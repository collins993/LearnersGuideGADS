package io.github.collins993.learnersguide.model

import io.github.collins993.learnersguide.db.entity.Courses
import java.io.Serializable

data class SuggestedCourses(
    val title: String? = "",
    val url: String? = "",
    var username: String? = "",
    var emailAddress: String? = "",
    var firstname: String? = "",
    var lastname: String? = "",
    var img: String? = "",
    var uid: String? = "",
    var facebookName: String? = "",
    var linkedlnName: String? = "",
    var githubName: String? = "",
    var date: Long? = -1
): Serializable

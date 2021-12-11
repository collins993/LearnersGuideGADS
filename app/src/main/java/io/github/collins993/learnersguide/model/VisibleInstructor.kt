package io.github.collins993.learnersguide.model


import com.google.gson.annotations.SerializedName

data class VisibleInstructor(
    @SerializedName("_class")
    val classX: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("image_100x100")
    val image100x100: String,
    @SerializedName("image_50x50")
    val image50x50: String,
    val initials: String,
    @SerializedName("job_title")
    val jobTitle: String,
    val name: String,
    val title: String,
    val url: String
)
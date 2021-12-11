package io.github.collins993.learnersguide.model


import com.google.gson.annotations.SerializedName

data class CourseResponse(
    val aggregations: List<Aggregation>,
    val count: Int,
    val next: String,
    val previous: Any,
    val results: MutableList<Result>,
    @SerializedName("search_tracking_id")
    val searchTrackingId: String
)
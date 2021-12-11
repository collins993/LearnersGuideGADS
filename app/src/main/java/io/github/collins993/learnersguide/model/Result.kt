package io.github.collins993.learnersguide.model


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("_class")
    val classX: String?,
    @SerializedName("curriculum_items")
    val curriculumItems: List<Any>?,
    @SerializedName("curriculum_lectures")
    val curriculumLectures: List<Any>?,
    val headline: String?,
    val id: Int?,
    @SerializedName("image_125_H")
    val image125H: String?,
    @SerializedName("image_240x135")
    val image240x135: String?,
    @SerializedName("image_480x270")
    val image480x270: String?,
    @SerializedName("input_features")
    val inputFeatures: Any?,
    @SerializedName("instructor_name")
    val instructorName: Any?,
    @SerializedName("is_paid")
    val isPaid: Boolean?,
    @SerializedName("is_practice_test_course")
    val isPracticeTestCourse: Boolean?,
    @SerializedName("lecture_search_result")
    val lectureSearchResult: Any?,
    @SerializedName("order_in_results")
    val orderInResults: Any?,
    @SerializedName("predictive_score")
    val predictiveScore: Any?,
    val price: String?,
    @SerializedName("price_detail")
    val priceDetail: PriceDetail?,
    @SerializedName("price_serve_tracking_id")
    val priceServeTrackingId: String?,
    @SerializedName("published_title")
    val publishedTitle: String?,
    @SerializedName("relevancy_score")
    val relevancyScore: Any?,
    val title: String?,
    @SerializedName("tracking_id")
    val trackingId: String?,
    val url: String?,
    @SerializedName("visible_instructors")
    val visibleInstructors: List<VisibleInstructor>?
)
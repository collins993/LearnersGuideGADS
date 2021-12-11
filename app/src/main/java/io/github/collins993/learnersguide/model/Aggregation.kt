package io.github.collins993.learnersguide.model


import com.google.gson.annotations.SerializedName

data class Aggregation(
    val id: String,
    val options: List<Option>,
    val title: String
)
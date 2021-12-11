package io.github.collins993.learnersguide.model


import com.google.gson.annotations.SerializedName

data class Option(
    val count: Int,
    val key: String,
    val title: String,
    val value: String
)
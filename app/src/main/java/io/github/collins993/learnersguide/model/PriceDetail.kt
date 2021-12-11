package io.github.collins993.learnersguide.model


import com.google.gson.annotations.SerializedName

data class PriceDetail(
    val amount: Double,
    val currency: String,
    @SerializedName("currency_symbol")
    val currencySymbol: String,
    @SerializedName("price_string")
    val priceString: String
)
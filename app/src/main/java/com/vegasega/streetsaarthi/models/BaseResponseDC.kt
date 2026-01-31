package com.vegasega.streetsaarthi.models

import com.google.gson.annotations.SerializedName


data class BaseResponseDC<T>(
    @SerializedName("data")
    val `data`: T? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("status_code")
    val statusCode: Int? = null,
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("success")
    val success: Boolean? = false,
    @SerializedName("vendor_id")
    val vendor_id: String? = null,
    @SerializedName("meta")
    val meta: Meta? = null,
)

data class Meta(
    val first_page: String,
    val last_page: String,
    val per_page: Int,
    val prev_page_url: String,
    val total_items: Int,
    val total_pages: Int
)
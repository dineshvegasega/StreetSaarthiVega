package com.vegasega.streetsaarthi.models

data class ItemLiveScheme(
    var description: String,
    val start_at: String,
    val end_at: String,
    var name: String,
    val scheme_id: Int,
    val scheme_image: SchemeImage,
    val status: String,
    var user_scheme_status: String,
//    var isRead : Boolean = false
)

package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class ErrorMessage(
    @Json(name = "msg") val message: String = ""
)

package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class Response(
    val status: String, @Json(name = "data") val document: Document
)

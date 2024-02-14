package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class Response(
    @Json(name = "status") val status: String,
    @Json(name = "data") val document: BarcodeData
)

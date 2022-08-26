package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class Document(
    val barcode: String,
    @Json(name = "doc_num") val number: String,
    @Json(name = "doc_status") val status: String
)

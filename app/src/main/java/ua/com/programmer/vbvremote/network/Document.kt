package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class Document(
    @Json(name = "barcode") val barcode: String,
    @Json(name = "doc_num") val number: String,
    @Json(name = "doc_status") val status: String,
    @Json(name = "msg") val message: String
)

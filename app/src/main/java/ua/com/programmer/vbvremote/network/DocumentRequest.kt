package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class DocumentRequest(
    @Json(name = "userid") val userid: String = "",
    @Json(name = "event") val event: String = "",
    @Json(name = "cut") val cut: Boolean,
    @Json(name = "data") val data: DocumentData = DocumentData(),
)

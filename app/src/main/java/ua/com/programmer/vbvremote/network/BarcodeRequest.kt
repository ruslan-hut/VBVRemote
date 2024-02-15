package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class BarcodeRequest (
    @Json(name = "barcode") val barcode: String = "",
    @Json(name = "userid") val userid: String = "",
    @Json(name = "event") val event: String = "",
    @Json(name = "cut") val cut: Boolean,
)
package ua.com.programmer.vbvremote.network

data class RequestBody (
    val barcode: String,
    val userid: String,
    val event: String,
    val cut: Boolean,
)
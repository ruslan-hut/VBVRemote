package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class Table(
    @Json(name = "table_number") val number: String = "",
    @Json(name = "date") val date: String = "",
)

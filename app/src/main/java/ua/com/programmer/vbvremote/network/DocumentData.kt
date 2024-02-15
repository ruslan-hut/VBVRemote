package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class DocumentData(
    @Json(name = "docs") val documents: List<Document> = emptyList(),
)

package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class ResponseData(
    @Json(name = "docs") val documents: List<Document> = emptyList(),
    @Json(name = "docs_plan") val plan: List<Document> = emptyList(),
    @Json(name = "tables") val tables: List<Table> = emptyList(),
)

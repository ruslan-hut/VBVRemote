package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class Document(
    @Json(name = "doc_num") val number: String = "",
    @Json(name = "doc_date") val date: String = "",
    @Json(name = "date_plan") val datePlan: String = "",
    @Json(name = "status") val status: String = "",
    @Json(name = "table_number") val table: String = "",
    @Json(name = "warn") val warn: Boolean = false,
    @Json(name = "products") val products: List<String> = emptyList(),
    var isExpanded: Boolean = false
)

fun Document.getContent(): String {
    //make list copy with added symbol on the beginning of each element
    val products = products.map { "- $it" }
    return products.joinToString("\n")
}

package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class AuthResponse(
    @Json(name = "status") val status: String = "",
    @Json(name = "data") val data: ResponseData? = null,
    @Json(name = "cut_dep") val isCutDepartment: Boolean = false,
    @Json(name = "boss") val isBoss: Boolean = false,
    @Json(name = "table") val table: String = "",
    @Json(name = "date_plan") val datePlan: String = "",
    @Json(name = "error") val errors: List<ErrorMessage> = emptyList()
)

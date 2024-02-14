package ua.com.programmer.vbvremote.network

import com.squareup.moshi.Json

data class AuthRequest (
    @Json(name = "userid") val userID: String,
)
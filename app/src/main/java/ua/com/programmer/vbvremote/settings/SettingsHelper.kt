package ua.com.programmer.vbvremote.settings

import android.content.Context
import java.util.*

const val BARCODE_KEY = "key_barcode"

class SettingsHelper(context: Context) {

    private val fileReference = "vbvremote_preferences"
    private val userIdKey = "userID"
    private val sharedPreferences = context.getSharedPreferences(fileReference, Context.MODE_PRIVATE)

    fun read(name: String): String {
        return sharedPreferences.getString(name, "") ?: return ""
    }

    fun write(name: String, value: String) {
        with(sharedPreferences.edit()) {
            putString(name, value)
            apply()
        }
    }

    fun userID(): String {
        return read(userIdKey)
    }

    init {
        var userID = read(userIdKey)
        if (userID.isBlank()) {
            userID = UUID.randomUUID().toString()
            write(userIdKey, userID)
        }
    }

}
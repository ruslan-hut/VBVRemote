package ua.com.programmer.vbvremote.settings

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.*

const val BARCODE_KEY = "key_barcode"

class SettingsHelper(context: Context) {

    private val fileReference = "vbvremote_preferences"
    private val userIdKey = "userID"
    private val sharedPreferences = context.getSharedPreferences(fileReference, Context.MODE_PRIVATE)

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

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

    fun baseUrl(): String {
        var url: String
        val server = preferences.getString("server_address", "") ?: ""
        val path = preferences.getString("api_path", "") ?: ""
        if (server.startsWith("http://") || server.startsWith("https://")) {
            url = server
        }else{
            url = "http://$server"
        }
        if (!url.endsWith("/", true)) url = "$url/"
        url = "$url$path"
        if (!url.endsWith("/", true)) url = "$url/"
        return url
    }

    init {
        var userID = read(userIdKey)
        if (userID.isBlank()) {
            userID = UUID.randomUUID().toString()
            write(userIdKey, userID)
        }
    }

}
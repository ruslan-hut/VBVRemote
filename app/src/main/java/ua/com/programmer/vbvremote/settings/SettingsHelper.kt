package ua.com.programmer.vbvremote.settings

import android.content.Context
import androidx.preference.PreferenceManager
import ua.com.programmer.vbvremote.BuildConfig
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
        if (BuildConfig.DEBUG) return "e3b64328-fb07-4d28-ba56-b8adb3f0f0d6"
        return read(userIdKey)
    }

    fun setConnectionDefaults() {
        preferences.edit().putString("server_address", "1c.onebyone.ua").apply()
        preferences.edit().putString("api_path", "1c/ru/hs/apiModel").apply()
    }

    fun baseUrl(): String {

        val server = preferences.getString("server_address", "") ?: ""
        val path = preferences.getString("api_path", "") ?: ""

        if (server.isBlank() && path.isBlank()) return ""

        var url: String
        url = if (server.startsWith("http://") || server.startsWith("https://")) {
            server
        }else{
            "http://$server"
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
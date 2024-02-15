package ua.com.programmer.vbvremote.shared

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ua.com.programmer.vbvremote.network.AuthRequest
import ua.com.programmer.vbvremote.network.AuthResponse
import ua.com.programmer.vbvremote.network.Event
import ua.com.programmer.vbvremote.network.RequestBody
import ua.com.programmer.vbvremote.network.Response
import ua.com.programmer.vbvremote.network.VBVApi
import ua.com.programmer.vbvremote.network.eventToString
import ua.com.programmer.vbvremote.settings.BARCODE_KEY
import ua.com.programmer.vbvremote.settings.SettingsHelper
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val settings: SettingsHelper
): ViewModel() {

    private var userId: String = ""
    private var baseUrl: String = ""
    private var api: VBVApi? = null

    private var apiResponse: AuthResponse? = null

    private val _authorized = MutableLiveData<Boolean>()
    val authorized: LiveData<Boolean>
        get() = _authorized

    private fun initApi(): Boolean {
        val currentId = settings.userID()
        val currentUrl = settings.baseUrl()
        if (currentId.isBlank() || currentUrl.isBlank()) {
            _authorized.value = false
            return false
        }
        if (currentId != userId || currentUrl != baseUrl) {
            userId = currentId
            baseUrl = currentUrl
            if (baseUrl.isBlank()) settings.setConnectionDefaults()
            api = VBVApi(baseUrl)
        }
        return api != null
    }

    private fun authenticate() {
        if (!initApi()) return
        if (_authorized.value == true) return
        val authRequest = AuthRequest(userId)
        viewModelScope.launch {
            try {
                apiResponse = api!!.retrofitService.authenticate(authRequest)
                _authorized.value = apiResponse?.status == "success"
                Log.d("PRG", "Authentication response: $apiResponse")
            } catch (e: Exception) {
                _authorized.value = false
                Log.e("PRG", "Authentication failed", e)
            }
        }
    }

    fun isAuthorized(): Boolean {
        val authState =  _authorized.value ?: false
        if (!authState) {
            authenticate()
        }
        return authState
    }

    fun isBoss(): Boolean {
        return apiResponse?.isBoss ?: false
    }

    private fun isCutDepartment(): Boolean {
        return apiResponse?.isCutDepartment ?: false
    }

    fun savedBarcode(): String {
        return settings.read(BARCODE_KEY)
    }

    fun getOrder(event: Event, onResponse: (Response?) -> Unit) {

        val requestBody = RequestBody(
            barcode = savedBarcode(),
            userid = userId,
            event = eventToString(event),
            cut = isCutDepartment(),
        )
        Log.d("PRG", "Request: $requestBody")

        viewModelScope.launch {
            try {
                val response = api?.retrofitService?.getOrder(requestBody)
                onResponse(response)
            }catch (e: java.lang.Exception){
                Log.d("PRG", "event: $event; failure: $e")
            }
        }

    }

}
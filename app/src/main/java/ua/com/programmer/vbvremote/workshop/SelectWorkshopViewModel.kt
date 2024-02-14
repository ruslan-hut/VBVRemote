package ua.com.programmer.vbvremote.workshop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.programmer.vbvremote.network.AuthRequest
import ua.com.programmer.vbvremote.network.VBVApi

class SelectWorkshopViewModel: ViewModel() {

    private lateinit var userId: String
    private lateinit var baseUrl: String
    private var api: VBVApi? = null

    private val _authorized = MutableLiveData<Boolean>()
    val authorized: LiveData<Boolean>
        get() = _authorized

    private fun authenticate() {
        if (api == null || userId.isBlank()) return
        if (_authorized.value == true) return
        val authRequest = AuthRequest(userId)
        viewModelScope.launch {
            try {
                val response = api!!.retrofitService.authenticate(authRequest)
                _authorized.value = response.status == "success"
                Log.d("PRG", "Authentication response: $response")
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

    fun setUserId(id: String) {
        userId = id
        authenticate()
    }

    fun setBaseUrl(url: String) {
        baseUrl = url

        if (baseUrl.isBlank()) return
        api = VBVApi(baseUrl)

        authenticate()
    }
}
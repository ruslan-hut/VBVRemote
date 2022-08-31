package ua.com.programmer.vbvremote.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.programmer.vbvremote.network.*
import java.lang.Exception

class LoginViewModel: ViewModel() {

    private lateinit var userId: String
    private var currentBarcode = ""

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _currentDocument = MutableLiveData<Document>()
    val currentDocument: LiveData<Document> = _currentDocument

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private val _number = MutableLiveData<String>()
    val number: LiveData<String>
        get() = _number

    init {
        resetDocumentData()
    }

    fun setUserId(id: String) {
        userId = id
    }

    fun setBarcode(value: String) {
        currentBarcode = value
    }

    fun requestData(event: Event) {
        doRequest(event)
    }

    fun resetDocumentData() {
        _number.value = ""
        _status.value = ""
        _message.value = ""
    }

    fun setStatus(text: String) {
        _status.value = text
    }

    private fun doRequest(event: Event) {
        _message.value = ""

        val requestBody = RequestBody(currentBarcode, userId, eventToString(Event.STATUS))

        viewModelScope.launch {
            try {
                val response = VBVApi.retrofitService.getOrder(requestBody)
                if (response.status == STATUS_OK) {
                    _currentDocument.value = response.document
                    _status.value = _currentDocument.value?.status
                    _number.value = _currentDocument.value?.number
                    _message.value = _currentDocument.value?.message
                } else {
                    _message.value = "Api response status: ${response.status}"
                    _status.value = ""
                }
            }catch (e: Exception){
                _message.value = "Failure: ${e.message}"
                _status.value = ""
                Log.d("XBUG", "event: $event; failure: $e")
            }
        }

    }

}
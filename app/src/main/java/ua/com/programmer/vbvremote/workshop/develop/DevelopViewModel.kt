package ua.com.programmer.vbvremote.develop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.programmer.vbvremote.network.*
import java.lang.Exception

class DevelopViewModel: ViewModel() {

    private lateinit var userId: String
    private lateinit var baseUrl: String
    private var api: VBVApi? = null
    private var workshop: String = ""

    private var _barcode = MutableLiveData<String>()
    //val barcode: LiveData<String>
    //    get() = _barcode

    private var _apiStatus = MutableLiveData<String>()
    val apiStatus: LiveData<String>
        get() = _apiStatus

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _currentDocument = MutableLiveData<BarcodeData>()
    //val currentDocument: LiveData<Document> = _currentDocument

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

    fun setWorkshop(value: String) {
        workshop = value
    }

    fun setBaseUrl(url: String) {
        baseUrl = url
        if (baseUrl.isNotBlank()) api = VBVApi(baseUrl)
    }

    fun setBarcode(value: String) {
        _barcode.value = value
    }

    fun requestData(event: Event) {
        doRequest(event)
    }

    fun resetDocumentData() {
        _number.value = ""
        _status.value = ""
        _message.value = ""
        _apiStatus.value = ""
    }

    fun setStatus(text: String) {
        _status.value = text
    }

    private fun doRequest(event: Event) {
        _message.value = ""
        val currentBarcode = _barcode.value
        if (currentBarcode.isNullOrBlank()) {
            _message.value = "NO BARCODE"
            return
        }

        val requestBody = RequestBody(
            barcode = currentBarcode,
            userid = userId,
            event = eventToString(event),
            cut = workshop == "cut",
            )
        Log.d("PRG", "Request: $requestBody")

        viewModelScope.launch {
            try {
                val response = api?.retrofitService?.getOrder(requestBody)

                resetDocumentData()
                _currentDocument.value = response?.document
                _message.value = _currentDocument.value?.message

                _apiStatus.value = response?.status

                Log.d("PRG", "Response: status: ${_apiStatus.value} ; data: ${_currentDocument.value}")

                if (_apiStatus.value == STATUS_OK) {
                    _status.value = _currentDocument.value?.status
                    _number.value = _currentDocument.value?.number
                }

            }catch (e: Exception){
                _message.value = "Failure: ${e.message}"
                _status.value = ""
                Log.d("PRG", "event: $event; failure: $e")
            }
        }

    }

}
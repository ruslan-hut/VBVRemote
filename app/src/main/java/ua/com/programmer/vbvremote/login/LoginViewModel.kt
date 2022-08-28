package ua.com.programmer.vbvremote.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ua.com.programmer.vbvremote.network.Barcode
import ua.com.programmer.vbvremote.network.Document
import ua.com.programmer.vbvremote.network.STATUS_OK
import ua.com.programmer.vbvremote.network.VBVApi
import java.lang.Exception

class LoginViewModel: ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _currentDocument = MutableLiveData<Document>()
    val currentDocument: LiveData<Document> = _currentDocument

    private val _number = MutableLiveData<String>()
    val number: LiveData<String>
        get() = _number

    init {
        resetDocumentData()
    }

    fun requestDataWithBarcode(barcodeValue: String){
        getOrder(barcodeValue)
    }

    fun resetDocumentData() {
        _number.value = ""
        _status.value = ""
    }

    fun setStatus(text: String) {
        _status.value = text
    }

    private fun getOrder(barcodeValue: String) {
        _number.value = ""

        val barcode = Barcode(barcodeValue)

        viewModelScope.launch {
            try {
                val response = VBVApi.retrofitService.getOrder(barcode)
                if (response.status == STATUS_OK) {
                    _currentDocument.value = response.document
                    _status.value = _currentDocument.value?.status
                    _number.value = _currentDocument.value?.number
                } else {
                    _status.value = "Api response status: ${response.status}"
                }
            }catch (e: Exception){
                _status.value = "Failure: ${e.message}"
                Log.d("XBUG", "getOrder: $e")
            }
        }
    }

}
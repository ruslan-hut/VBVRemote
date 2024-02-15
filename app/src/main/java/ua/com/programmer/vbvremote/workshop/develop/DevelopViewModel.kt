package ua.com.programmer.vbvremote.workshop.develop

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.com.programmer.vbvremote.network.*
import javax.inject.Inject

@HiltViewModel
class DevelopViewModel @Inject constructor(): ViewModel() {

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

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    private val _number = MutableLiveData<String>()
    val number: LiveData<String>
        get() = _number

    init {
        resetDocumentData()
    }

    fun setBarcode(value: String) {
        _barcode.value = value
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

    fun onResult(response: BarcodeResponse?) {
        resetDocumentData()
        _currentDocument.value = response?.document
        _message.value = _currentDocument.value?.message

        _apiStatus.value = response?.status

        if (_apiStatus.value == STATUS_OK) {
            _status.value = _currentDocument.value?.status
            _number.value = _currentDocument.value?.number
        }
    }

}
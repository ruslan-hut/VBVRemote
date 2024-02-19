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
import ua.com.programmer.vbvremote.network.Document
import ua.com.programmer.vbvremote.network.Event
import ua.com.programmer.vbvremote.network.BarcodeRequest
import ua.com.programmer.vbvremote.network.DocumentData
import ua.com.programmer.vbvremote.network.BarcodeResponse
import ua.com.programmer.vbvremote.network.DocumentRequest
import ua.com.programmer.vbvremote.network.DocumentResponse
import ua.com.programmer.vbvremote.network.ErrorMessage
import ua.com.programmer.vbvremote.network.ResponseData
import ua.com.programmer.vbvremote.network.STATUS_ERROR
import ua.com.programmer.vbvremote.network.Table
import ua.com.programmer.vbvremote.network.VBVApi
import ua.com.programmer.vbvremote.network.eventToString
import ua.com.programmer.vbvremote.settings.BARCODE_KEY
import ua.com.programmer.vbvremote.settings.SettingsHelper
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val settings: SettingsHelper
): ViewModel() {

    private var userId: String = ""
    private var baseUrl: String = ""
    private var api: VBVApi? = null

    private var apiResponse: AuthResponse? = null

    var selectedDocuments: List<Document> = listOf()
    var tables: List<Table> = listOf()

    private val _authorized = MutableLiveData<Boolean>()
    val authorized: LiveData<Boolean>
        get() = _authorized

    private val _documentsPlan = MutableLiveData<List<Document>>()
    val documentsPlan: LiveData<List<Document>>
        get() = _documentsPlan

    private val _documentsWork = MutableLiveData<List<Document>>()
    val documentsWork: LiveData<List<Document>>
        get() = _documentsWork

    private val _documentsStatus = MutableLiveData<List<Document>>()
    val documentsStatus: LiveData<List<Document>>
        get() = _documentsStatus

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

    private fun loadUserData(data: ResponseData) {
        _documentsPlan.value = data.plan ?: emptyList()
        _documentsWork.value = data.documents ?: emptyList()
        _documentsStatus.value = data.status ?: emptyList()
        selectedDocuments = emptyList()
        tables = data.tables ?: emptyList()
    }

    fun isAuthorized(): Boolean {
        val authState =  _authorized.value ?: false
        if (!authState) {
            authenticate{}
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

    fun authenticate(onResponse: (AuthResponse) -> Unit) {
        if (!initApi()) {
            val response = AuthResponse(
                status = STATUS_ERROR,
                errors = listOf(ErrorMessage("Помилка в налаштуваннях підключення"))
            )
            onResponse(response)
            return
        }
        val authRequest = AuthRequest(userId)
        viewModelScope.launch {
            try {

                apiResponse = api!!.retrofitService.authenticate(authRequest)

                apiResponse?.let {
                    _authorized.value = it.status == "success"
                    if (it.data != null) loadUserData(apiResponse!!.data!!)
                    onResponse(it)
                }

            } catch (e: Exception) {
                _authorized.value = false
                Log.e("PRG", "Authentication failed", e)
                val response = AuthResponse(
                    status = STATUS_ERROR,
                )
                onResponse(response)
            }
        }
    }

    fun barcode(event: Event, onResponse: (BarcodeResponse?) -> Unit) {

        val requestBody = BarcodeRequest(
            barcode = savedBarcode(),
            userid = userId,
            event = eventToString(event),
            cut = isCutDepartment(),
        )

        viewModelScope.launch {
            try {
                val response = api?.retrofitService?.barcode(requestBody)
                onResponse(response)
            }catch (e: java.lang.Exception){
                Log.e("PRG", "event: $event; failure: $e")
            }
        }

    }

    fun processDocumentsAuto(documents: List<Document>, onResponse: (DocumentResponse?) -> Unit) {

        val requestBody = DocumentRequest(
            userid = userId,
            event = "automatic_distrib",
            cut = isCutDepartment(),
            data = DocumentData(documents)
        )

        viewModelScope.launch {
            try {
                val response = api?.retrofitService?.documents(requestBody)
                if (response?.data != null) loadUserData(response.data)
                onResponse(response)
            }catch (e: java.lang.Exception){
                Log.e("PRG", "processDocumentsAuto: failure: $e")
                val response = DocumentResponse(
                    status = STATUS_ERROR,
                )
                onResponse(response)
            }
        }

    }

    fun processDocumentsManual(documents: List<Document>, onResponse: (DocumentResponse?) -> Unit) {

        val requestBody = DocumentRequest(
            userid = userId,
            event = "manual_distrib",
            cut = isCutDepartment(),
            data = DocumentData(documents)
        )

        viewModelScope.launch {
            try {
                val response = api?.retrofitService?.documents(requestBody)
                if (response?.data != null) loadUserData(response.data)
                onResponse(response)
            }catch (e: java.lang.Exception){
                Log.e("PRG", "processDocumentsManual: failure: $e")
                val response = DocumentResponse(
                    status = STATUS_ERROR,
                )
                onResponse(response)
            }
        }

    }

    fun updateDocument(document: Document, onResponse: (DocumentResponse?) -> Unit) {

        val requestBody = DocumentRequest(
            userid = userId,
            event = "cut_work",
            cut = isCutDepartment(),
            data = DocumentData(List(1) { document })
        )

        viewModelScope.launch {
            try {
                val response = api?.retrofitService?.documents(requestBody)
                if (response?.data != null) loadUserData(response.data)
                onResponse(response)
            }catch (e: java.lang.Exception){
                Log.e("PRG", "updateDocument: failure: $e")
                val response = DocumentResponse(
                    status = STATUS_ERROR,
                )
                onResponse(response)
            }
        }

    }

    fun getTableDate(tableNumber: String): String {
        return tables.find { it.number == tableNumber }?.date ?: ""
    }

    fun isValidData(tableNumber: String, date: String, time: String): Boolean {
        val table = tables.find { it.number == tableNumber } ?: return false
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateTime = "$date $time:00"
        return try {
            val tableDate = formatter.parse(table.date)
            val selectedDate = formatter.parse(dateTime)
            selectedDate?.after(tableDate) ?: false
        }catch (e: Exception){
            Log.e("PRG", "isValidData: $e")
            false
        }
    }

}
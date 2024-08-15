package ua.com.programmer.vbvremote.workshop.boss

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import dagger.hilt.android.lifecycle.HiltViewModel
import ua.com.programmer.vbvremote.network.Document
import javax.inject.Inject

@HiltViewModel
class BossViewModel @Inject constructor(): ViewModel() {

    private val showPlanned = MutableLiveData(false)
    private val docToPlan = MutableLiveData<List<Document>>(listOf())
    private val docPlanned = MutableLiveData<List<Document>>(listOf())

    val currentList = showPlanned.switchMap {
        if (it) docPlanned else docToPlan
    }
    val isPlannedList get() = showPlanned

    fun onDocumentsPlanReceived(documents: List<Document>) {
        Log.d("BossViewModel", "onDocumentsPlanReceived: ${documents.size}")
        docToPlan.value = documents
    }

    fun onDocumentsStatusReceived(documents: List<Document>) {
        docPlanned.value = documents
    }

    fun switchPlanned() {
        showPlanned.value = !showPlanned.value!!
    }
}
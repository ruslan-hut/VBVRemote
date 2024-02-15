package ua.com.programmer.vbvremote.workshop.boss

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
    val docToWork = MutableLiveData<List<Document>>(listOf())

    val currentList = showPlanned.switchMap {
        if (it) docPlanned else docToPlan
    }
    val isPlannedList get() = showPlanned

    fun onDocumentsReceived(documents: List<Document>) {
        val toPlan = mutableListOf<Document>()
        val planned = mutableListOf<Document>()

        documents.forEach {
            if (it.datePlan.isNotEmpty()) {
                planned.add(it)
            } else {
                toPlan.add(it)
            }
        }

        docToPlan.value = toPlan
        docPlanned.value = planned

    }

    fun switchPlanned() {
        showPlanned.value = !showPlanned.value!!
    }
}
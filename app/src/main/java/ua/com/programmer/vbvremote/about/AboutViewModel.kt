package ua.com.programmer.vbvremote.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ua.com.programmer.vbvremote.BuildConfig

class AboutViewModel: ViewModel() {

    private val _version = MutableLiveData<String>()
    val version: LiveData<String>
        get() = _version

    private var _userID = ""
    val userID: String
        get() = _userID

    init {
        _version.value = BuildConfig.VERSION_NAME
    }

    fun setUserID(value: String) {
        _userID = value
    }
}
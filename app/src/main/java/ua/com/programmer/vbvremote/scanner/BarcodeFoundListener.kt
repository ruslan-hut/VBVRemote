package ua.com.programmer.vbvremote.scanner

interface BarcodeFoundListener {
    fun onBarcodeFound(value: String?)
}
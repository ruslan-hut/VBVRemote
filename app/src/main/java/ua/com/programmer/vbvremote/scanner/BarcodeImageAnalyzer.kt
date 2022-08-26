package ua.com.programmer.vbvremote.scanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeImageAnalyzer(barcodeFoundListener: BarcodeFoundListener): ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private val listener = barcodeFoundListener

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        listener.onBarcodeFound(barcode.rawValue)
                    }
                }
                .addOnCompleteListener {
                    run {
                        imageProxy.close()
                        mediaImage.close()
                    }
                }
        }else{
            imageProxy.close()
        }
    }
}
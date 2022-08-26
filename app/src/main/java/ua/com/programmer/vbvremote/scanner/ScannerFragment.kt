package ua.com.programmer.vbvremote.scanner

import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.common.util.concurrent.ListenableFuture
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentScannerBinding
import ua.com.programmer.vbvremote.settings.BARCODE_KEY
import ua.com.programmer.vbvremote.settings.SettingsHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerFragment : Fragment() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ListenableFuture<ProcessCameraProvider>
    private lateinit var binding: FragmentScannerBinding

    private lateinit var settings: SettingsHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scanner, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        settings = SettingsHelper(context)
        settings.write(BARCODE_KEY, "")

        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProvider = ProcessCameraProvider.getInstance(context)

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
            if (isGranted) {
                setupCamera()
            }else{
                activity?.onBackPressed()
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            } else {
                setupCamera()
            }
        }else{
            setupCamera()
        }

    }

    private fun setupCamera() {

        val context = requireContext()

        val barcodeImageAnalyzer = BarcodeImageAnalyzer(object : BarcodeFoundListener {
            override fun onBarcodeFound(value: String?) {
                onScannerResult(value)
            }
        })

        cameraProvider.addListener({
            try {

                val provider = cameraProvider.get()
                val preview = Preview.Builder().build()

                preview.setSurfaceProvider(binding.previewView.surfaceProvider)

                val imageAnalyzer = ImageAnalysis.Builder().build()
                imageAnalyzer.setAnalyzer(cameraExecutor, barcodeImageAnalyzer)

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                provider.unbindAll()
                provider.bindToLifecycle(this, cameraSelector, imageAnalyzer, preview)

            }catch (e: Exception){
                Log.e("XBUG", "Camera setup failure: $e")
            }
        }, ContextCompat.getMainExecutor(context))

    }

    private fun stopCamera() {
        try {
            cameraProvider.get().unbindAll()
        }catch (e: Exception){
            Log.e("XBUG", "Stop camera failure: $e")
        }
    }

    private fun onScannerResult(value: String?) {
        stopCamera()

        ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100).startTone(ToneGenerator.TONE_PROP_BEEP)

        if (value != null) settings.write(BARCODE_KEY, value)
        activity?.onBackPressed()
    }
}

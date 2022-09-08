package ua.com.programmer.vbvremote.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentLoginBinding
import ua.com.programmer.vbvremote.network.Event
import ua.com.programmer.vbvremote.network.STATUS_ERROR
import ua.com.programmer.vbvremote.settings.BARCODE_KEY
import ua.com.programmer.vbvremote.settings.SettingsHelper

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private lateinit var settings: SettingsHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.submit.setOnClickListener { requestDataWithBarcode() }
        binding.pause.setOnClickListener { requestJobPause() }
        binding.start.setOnClickListener { requestJobStart() }
        binding.finish.setOnClickListener { requestJobFinish() }

        binding.barcodeField.setEndIconOnClickListener {
            binding.textBarcodeEditText.setText("")
            viewModel.resetDocumentData()
            findNavController().navigate(R.id.action_loginFragment_to_scannerFragment)
        }

        settings = SettingsHelper(requireContext())
        viewModel.setUserId(settings.userID())

        if (settings.baseUrl().isBlank()) settings.setConnectionDefaults()

        viewModel.apiStatus.observe(viewLifecycleOwner) {
            if (it == STATUS_ERROR) {
                showErrorStatus()
            }
        }
    }

    private fun showErrorStatus() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(getString(R.string.operation_cancelled, viewModel.message.value.toString()))
            .setCancelable(false)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun requestDataWithBarcode() {
        viewModel.resetDocumentData()
        viewModel.setStatus(getString(R.string.api_request_text))
        viewModel.setBarcode(binding.textBarcodeEditText.text.toString())
        viewModel.requestData(Event.STATUS)
    }

    private fun requestJobPause() {
        viewModel.requestData(Event.PAUSE)
    }

    private fun requestJobStart() {
        viewModel.requestData(Event.START)
    }

    private fun requestJobFinish() {
        viewModel.requestData(Event.STOP)
    }

    override fun onResume() {
        super.onResume()

        val baseUrl = settings.baseUrl()
        if (baseUrl.isBlank()) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.warning)
                .setMessage(
                    R.string.error_no_connection_settings
                )
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
        }
        viewModel.setBaseUrl(baseUrl)

        val currentText = binding.textBarcodeEditText.text.toString()
        if (currentText.isBlank()) {
            val barcode = settings.read(BARCODE_KEY)
            if (barcode.isNotBlank()) {
                viewModel.setBarcode(barcode)
                binding.textBarcodeEditText.setText(barcode)
                requestDataWithBarcode()
            }
        }
    }
}
package ua.com.programmer.vbvremote.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentLoginBinding
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
        binding.textBarcodeEditText.setText(R.string.default_barcode)

        binding.submit.setOnClickListener { requestDataWithBarcode() }

        binding.barcodeField.setEndIconOnClickListener {
            binding.textBarcodeEditText.setText("")
            viewModel.resetDocumentData()
            findNavController().navigate(R.id.action_loginFragment_to_scannerFragment)
        }

        settings = SettingsHelper(requireContext())
    }

    private fun requestDataWithBarcode() {
        viewModel.setStatus(getString(R.string.api_request_text))
        val barcode = binding.textBarcodeEditText.text.toString()
        viewModel.requestDataWithBarcode(barcode)
    }

    override fun onResume() {
        super.onResume()

        val currentText = binding.textBarcodeEditText.text.toString()
        if (currentText.isBlank()) {
            val barcode = settings.read(BARCODE_KEY)
            if (barcode.isNotBlank()) binding.textBarcodeEditText.setText(barcode)
        }
    }
}
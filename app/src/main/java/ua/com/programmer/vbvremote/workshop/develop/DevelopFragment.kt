package ua.com.programmer.vbvremote.workshop.develop

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentLoginBinding
import ua.com.programmer.vbvremote.network.Event
import ua.com.programmer.vbvremote.network.STATUS_ERROR
import ua.com.programmer.vbvremote.shared.SharedViewModel

@AndroidEntryPoint
class DevelopFragment : Fragment() {

    private val shared: SharedViewModel by activityViewModels()
    private val viewModel: DevelopViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

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

        binding.workshop.text = getString(R.string.workshop_develop)

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
        shared.barcode(Event.STATUS, viewModel::onResult)
    }

    private fun requestJobPause() {
        shared.barcode(Event.PAUSE, viewModel::onResult)
    }

    private fun requestJobStart() {
        shared.barcode(Event.START, viewModel::onResult)
    }

    private fun requestJobFinish() {
        shared.barcode(Event.STOP, viewModel::onResult)
    }

    override fun onResume() {
        super.onResume()

        val currentText = binding.textBarcodeEditText.text.toString()
        if (currentText.isBlank()) {
            val barcode = shared.savedBarcode()
            if (barcode.isNotBlank()) {
                viewModel.setBarcode(barcode)
                binding.textBarcodeEditText.setText(barcode)
                requestDataWithBarcode()
            }
        }
    }
}
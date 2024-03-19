package ua.com.programmer.vbvremote.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentDateTimeBinding
import ua.com.programmer.vbvremote.network.Document
import ua.com.programmer.vbvremote.network.DocumentResponse
import ua.com.programmer.vbvremote.network.STATUS_ERROR
import ua.com.programmer.vbvremote.shared.SharedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class DateTimePicker: Fragment() {

    private  val shared: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentDateTimeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_date_time, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //binding.sharedViewModel = shared
        binding.lifecycleOwner = viewLifecycleOwner

        if (shared.tables.size == 1) {
            onTableSelected(shared.tables[0].number)
        }

        binding.save.setOnClickListener {
            if (!allDataSet()) {
                showWarningNotSelected()
            }
        }
        binding.date.setOnClickListener {
            showDateDialog()
        }
        binding.time.setOnClickListener {
            showTimeDialog()
        }
        binding.table.setOnClickListener {
            showTableDialog()
        }
        binding.reset.setOnClickListener {
            resetDateTime()
        }
        binding.tableDate.setOnClickListener {
            copyDateTime()
        }
    }

    private fun allDataSet(): Boolean {
        val tableNumber = binding.table.text.toString()
        val date = binding.date.text.toString()
        val time = binding.time.text.toString()
        if (tableNumber == "?" || date == "?" || time == "?") {
            return false
        }
        if (!shared.isValidData(tableNumber, date, time)) {
            return false
        }

        val dateTime = "$date $time"
        val documents = shared.selectedDocuments.map {
            Document(
                number = it.number,
                date = it.date,
                datePlan = dateTime,
                table = tableNumber,
            )
        }
        shared.processDocumentsManual(documents) {
            if (it == null) {
                showNoResponse()
            } else {
                showResponseResult(it)
            }
        }
        return true
    }

    private fun showResponseResult(response: DocumentResponse) {
        if (response.status == STATUS_ERROR) {
            val error = response.errors.firstOrNull()?.message ?: ""
            if (error.isNotEmpty()) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.warning)
                    .setMessage(error)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            } else {
                showNoResponse()
            }
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showNoResponse() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(
                R.string.error_no_response
            )
            .setCancelable(false)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun showTableDialog() {
        val items = shared.tables.map { it.number }
        val checkedItems = BooleanArray(items.size)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_table)
            .setSingleChoiceItems(items.toTypedArray(), 0) { _, which ->
                Log.d("PRG", "showTableDialog: $which")
            }
            .setPositiveButton(R.string.ok) { _, _ ->
                val selectedTables = mutableListOf<String>()
                for (i in checkedItems.indices) {
                    selectedTables.add(items[i])
                }
                if (selectedTables.isNotEmpty()) {
                    onTableSelected(selectedTables[0])
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showTimeDialog() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setHour(12)
            .setMinute(0)
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = String.format("%02d", timePicker.hour)
            val minute = String.format("%02d", timePicker.minute)
            val time = "$hour:$minute"
            binding.time.text = time
        }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun showDateDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener {
            binding.date.text = formatUnixTimestamp(it)
        }
        datePicker.show(parentFragmentManager, "datePicker")
    }

    private fun showWarningNotSelected() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(
                R.string.select_all_parameters
            )
            .setCancelable(false)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun onTableSelected(tableNumber: String) {
        binding.table.text = tableNumber
        binding.tableDate.text = shared.getTableDate(tableNumber)
    }

    private fun resetDateTime() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(
                R.string.reset_date_time
            )
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { _, _ ->
                binding.date.text = "?"
                binding.time.text = "?"
            }
            .show()
    }

    private fun copyDateTime() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(
                R.string.set_available_date
            )
            .setCancelable(true)
            .setPositiveButton(R.string.ok) { _, _ ->
                val dateTime = binding.tableDate.text.toString()
                // split date and time, example: 2021-10-01 15:00:00
                val parts = dateTime.split(" ")
                if (parts.size == 2) {
                    binding.date.text = parts[0]
                    // remove ending 00 from time like 15:00:00
                    val time = parts[1].split(":")
                    if (time.size == 3) {
                         val timeText = "${time[0]}:${time[1]}"
                        binding.time.text = timeText
                    } else {
                        binding.time.text = "?"
                    }
                } else {
                    binding.date.text = "?"
                    binding.time.text = "?"
                }
            }
            .show()
    }

    private fun formatUnixTimestamp(unixTimestamp: Long): String {
        val date = Date(unixTimestamp) // Convert seconds to milliseconds
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(date)
    }
}

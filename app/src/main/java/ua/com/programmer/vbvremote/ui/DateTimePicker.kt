package ua.com.programmer.vbvremote.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentDateTimeBinding
import ua.com.programmer.vbvremote.shared.SharedViewModel
import java.util.Calendar

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
            binding.table.text = shared.tables[0].number
        }

        binding.save.setOnClickListener {
            showWarningNotSelected()
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
                    binding.table.text = selectedTables.joinToString()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showTimeDialog() {
        val c = Calendar.getInstance()
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(c.get(Calendar.HOUR_OF_DAY))
            .setMinute(c.get(Calendar.MINUTE))
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute
            val time = "$hour:$minute"
            binding.time.text = time
        }
        timePicker.show(parentFragmentManager, "timePicker")
    }

    private fun showDateDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener {
            binding.date.text = datePicker.headerText
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
}

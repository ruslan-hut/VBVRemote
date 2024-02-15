package ua.com.programmer.vbvremote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
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

        binding.save.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.date.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                binding.date.text = datePicker.headerText
            }
            datePicker.show(parentFragmentManager, "datePicker")
        }
        binding.time.setOnClickListener {
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
    }
}

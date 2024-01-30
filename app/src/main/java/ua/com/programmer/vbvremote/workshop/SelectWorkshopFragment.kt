package ua.com.programmer.vbvremote.workshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentSelectWorkshopBinding
import ua.com.programmer.vbvremote.settings.SettingsHelper

class SelectWorkshopFragment: Fragment() {

    private val viewModel: SelectWorkshopViewModel by viewModels()
    private lateinit var binding: FragmentSelectWorkshopBinding

    private lateinit var settings: SettingsHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_workshop, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectWorkshopViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        settings = SettingsHelper(requireContext())

        binding.btnCut.setOnClickListener {
            settings.write("workshop", "cut")
            findNavController().navigate(R.id.action_selectWorkshopFragment_to_loginFragment)
        }

        binding.btnDevelop.setOnClickListener {
            settings.write("workshop", "develop")
            findNavController().navigate(R.id.action_selectWorkshopFragment_to_loginFragment)
        }
    }
}

package ua.com.programmer.vbvremote.about

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentAboutBinding
import ua.com.programmer.vbvremote.settings.SettingsHelper

class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding
    private val viewModel: AboutViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = SettingsHelper(requireContext())
        viewModel.setUserID(settings.userID())

        binding.aboutViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }
}

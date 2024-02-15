package ua.com.programmer.vbvremote.workshop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentSelectWorkshopBinding
import ua.com.programmer.vbvremote.shared.SharedViewModel

class SelectWorkshopFragment: Fragment() {

    private  val shared: SharedViewModel by activityViewModels()
    private val viewModel: SelectWorkshopViewModel by viewModels()
    private lateinit var binding: FragmentSelectWorkshopBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_workshop, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.selectWorkshopViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnCut.setOnClickListener {
            if (shared.isAuthorized()) {
                if (shared.isBoss()) {
                    findNavController().navigate(R.id.action_selectWorkshopFragment_to_bossFragment)
                } else {
                    findNavController().navigate(R.id.action_selectWorkshopFragment_to_cutFragment)
                }
            } else {
                findNavController().navigate(R.id.action_selectWorkshopFragment_to_dateTimePicker)
                //showNotAuthorizedDialog()
            }
        }

        binding.btnDevelop.setOnClickListener {
            if (shared.isAuthorized()) {
                findNavController().navigate(R.id.action_selectWorkshopFragment_to_loginFragment)
            } else {
                showNotAuthorizedDialog()
            }
        }

        shared.authorized.observe(viewLifecycleOwner) {
            binding.authState.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun showNotAuthorizedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(
                R.string.auth_error
            )
            .setCancelable(false)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

}
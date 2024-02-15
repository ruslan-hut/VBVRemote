package ua.com.programmer.vbvremote.workshop.boss

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentDocumentsListBinding
import ua.com.programmer.vbvremote.network.DocumentResponse
import ua.com.programmer.vbvremote.network.STATUS_ERROR
import ua.com.programmer.vbvremote.shared.SharedViewModel

@AndroidEntryPoint
class DocumentsPlan: Fragment() {

    private val shared: SharedViewModel by activityViewModels()
    private val viewModel: BossViewModel by viewModels()
    private lateinit var binding: FragmentDocumentsListBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_documents_list, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bossViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        recyclerView = binding.documentsList
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = ListAdapterPlan(
            onItemClicked = { },
            onItemLongClicked = { }
        )
        recyclerView.adapter = adapter

        shared.documentsPlan.observe(viewLifecycleOwner) {
            viewModel.onDocumentsReceived(it)
        }
        viewModel.currentList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                binding.noData.visibility = View.VISIBLE
            } else {
                binding.noData.visibility = View.GONE
            }
        }
        viewModel.isPlannedList.observe(viewLifecycleOwner) {
            if (it) {
                binding.modeSelect.visibility = View.GONE
            } else {
                binding.modeSelect.visibility = View.VISIBLE
            }
        }

        binding.modeAuto.setOnClickListener {
            if (adapter.selectedDocuments.isEmpty()) {
                showNothingToSend()
                return@setOnClickListener
            }
            binding.progress.visibility = View.VISIBLE
            shared.processDocumentsAuto(adapter.selectedDocuments) {
                binding.progress.visibility = View.GONE
                if (it == null) {
                    showNoResponse()
                } else {
                    showResponseResult(it)
                }
            }
        }
        binding.modeManual.setOnClickListener {
            if (adapter.selectedDocuments.isEmpty()) {
                showNothingToSend()
                return@setOnClickListener
            }
//            binding.progress.visibility = View.VISIBLE
//            shared.processDocumentsAuto(adapter.selectedDocuments) {
//                binding.progress.visibility = View.GONE
//                if (it == null) {
//                    showNoResponse()
//                } else {
//                    showResponseResult(it)
//                }
//            }
        }
        binding.showDocuments.setOnClickListener {
            viewModel.switchPlanned()
        }
    }

    private fun showNothingToSend() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.warning)
            .setMessage(
                R.string.error_nothing_selected
            )
            .setCancelable(false)
            .setPositiveButton(R.string.ok, null)
            .show()
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
}
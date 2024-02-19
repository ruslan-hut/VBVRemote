package ua.com.programmer.vbvremote.workshop.boss

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import ua.com.programmer.vbvremote.network.Document
import ua.com.programmer.vbvremote.network.DocumentResponse
import ua.com.programmer.vbvremote.network.STATUS_OK
import ua.com.programmer.vbvremote.shared.SharedViewModel

@AndroidEntryPoint
class DocumentsWork: Fragment() {

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
            onItemLongClicked = { showDocumentActions(it) }
        )
        recyclerView.adapter = adapter

        shared.documentsWork.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.bottomBar.visibility = View.GONE

    }

    private fun showDocumentActions(document: Document) {
        val items = List(3) {
            when (it) {
                0 -> getString(R.string.start)
                1 -> getString(R.string.pause)
                else -> getString(R.string.finish)
            }
        }
        var checkedItem = when(document.status) {
            "start" -> 0
            "pause" -> 1
            else -> 2
        }
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.change_status)
            .setSingleChoiceItems(items.toTypedArray(), checkedItem) { _, which ->
                checkedItem = which
            }
            .setPositiveButton(R.string.ok) { _, _ ->
                val newStatus = when(checkedItem) {
                    0 -> "start"
                    1 -> "pause"
                    else -> "stop"
                }
                val updated = document.copy(status = newStatus)
                updateStatus(updated)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun updateStatus(document: Document) {
        shared.updateDocument(document) {
            if (it == null) {
                showNoResponse()
            } else {
                showResponseResult(it)
            }
        }
    }

    private fun showResponseResult(response: DocumentResponse) {
        if (response.status != STATUS_OK) {
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
            Toast.makeText(requireContext(), R.string.success, Toast.LENGTH_SHORT).show()
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
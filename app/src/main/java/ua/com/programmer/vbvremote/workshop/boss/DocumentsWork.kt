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
import dagger.hilt.android.AndroidEntryPoint
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.FragmentDocumentsListBinding
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
            onItemLongClicked = { }
        )
        recyclerView.adapter = adapter

        shared.documentsWork.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.bottomBar.visibility = View.GONE

    }

}
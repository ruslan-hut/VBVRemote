package ua.com.programmer.vbvremote.workshop.boss

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ua.com.programmer.vbvremote.R
import ua.com.programmer.vbvremote.databinding.ElementDocumentPlanBinding
import ua.com.programmer.vbvremote.databinding.ElementDocumentPlannedBinding
import ua.com.programmer.vbvremote.databinding.ElementDocumentPlannedWarnBinding
import ua.com.programmer.vbvremote.network.Document

class ListAdapterPlan (
    private val onItemClicked: (Document) -> Unit,
    private val onItemLongClicked: (Document) -> Unit)
    : ListAdapter<Document, ListAdapterPlan.ItemViewHolder>(DiffCallback) {

    private var isClickable = true
    val selectedDocuments: MutableList<Document> = mutableListOf()

    fun setClickable(isClickable: Boolean) {
        this.isClickable = isClickable
    }

    abstract class ItemViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item: Document, isMarked: Boolean, onSelected: (Document, Boolean) -> Unit)
    }

    class DocumentPlan(private var binding: ElementDocumentPlanBinding): ItemViewHolder(binding as ViewDataBinding) {
        override fun bind(item: Document, isMarked: Boolean, onSelected: (Document, Boolean) -> Unit) {
            binding.apply {
                docNumber.text = item.number
                docDate.text = item.date
                docMark.isChecked = isMarked
                docMark.setOnClickListener {
                    onSelected(item, docMark.isChecked)
                }
            }
        }
    }

    class DocumentPlanned(private var binding: ElementDocumentPlannedBinding): ItemViewHolder(binding as ViewDataBinding) {
        override fun bind(item: Document, isMarked: Boolean, onSelected: (Document, Boolean) -> Unit) {
            binding.apply {
                docNumber.text = item.number
                docDate.text = item.date
                docDatePlan.text = item.datePlan
                table.text = item.table
                statusIcon.setImageResource(
                    when (item.status) {
                        "start" -> R.drawable.baseline_play_arrow_24
                        "stop" -> R.drawable.baseline_stop_24
                        "pause" -> R.drawable.baseline_pause_24
                        else -> R.drawable.baseline_question_mark_24
                    }
                )
            }
        }
    }

    class DocumentPlannedWarn(private var binding: ElementDocumentPlannedWarnBinding): ItemViewHolder(binding as ViewDataBinding) {
        override fun bind(item: Document, isMarked: Boolean, onSelected: (Document, Boolean) -> Unit) {
            binding.apply {
                docNumber.text = item.number
                docDate.text = item.date
                docDatePlan.text = item.datePlan
                table.text = item.table
                statusIcon.setImageResource(
                    when (item.status) {
                        "start" -> R.drawable.baseline_play_arrow_24
                        "stop" -> R.drawable.baseline_stop_24
                        "pause" -> R.drawable.baseline_pause_24
                        else -> R.drawable.baseline_question_mark_24
                    }
                )
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Document>() {

            override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem.number == newItem.number
                        && oldItem.status == newItem.status
                        && oldItem.datePlan == newItem.datePlan
            }

            override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        if (item.datePlan.isEmpty()) return 0
        return if (item.warn) 2 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val viewHolder = when(viewType) {
            0 -> DocumentPlan(
                ElementDocumentPlanBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            1 -> DocumentPlanned(
                ElementDocumentPlannedBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
            else -> DocumentPlannedWarn(
                ElementDocumentPlannedWarnBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

        viewHolder.itemView.setOnClickListener {
            if (!isClickable) return@setOnClickListener
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }

        viewHolder.itemView.setOnLongClickListener {
            if (!isClickable) return@setOnLongClickListener(true)
            val position = viewHolder.adapterPosition
            onItemLongClicked(getItem(position))
            true
        }

        return viewHolder
    }

    private fun onSelect(item: Document, selected: Boolean) {
        if (selected) {
            selectedDocuments.add(item)
        } else {
            selectedDocuments.remove(item)
        }
    }

    private fun isSelected(item: Document): Boolean {
        return selectedDocuments.contains(item)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, isSelected(item), this::onSelect)
    }
}
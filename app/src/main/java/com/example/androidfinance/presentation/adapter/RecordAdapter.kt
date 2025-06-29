package com.example.androidfinance.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinance.R
import com.example.androidfinance.data.entity.RecordType
import com.example.androidfinance.databinding.ItemRecordBinding
import com.example.androidfinance.domain.model.Record
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class RecordAdapter(
    private val onEditClick: (Record) -> Unit,
    private val onDeleteClick: (Record) -> Unit,
    private val onFavoriteClick: (Record, Boolean) -> Unit
) : ListAdapter<Record, RecordAdapter.RecordViewHolder>(RecordDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val binding = ItemRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecordViewHolder(
        private val binding: ItemRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        private val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        fun bind(record: Record) {
            binding.apply {
                tvDescription.text = record.description
                tvAmount.text = currencyFormatter.format(record.amount)
                tvCategory.text = record.category
                tvDate.text = dateFormatter.format(record.date)

                // Set type background color
                tvType.text = if (record.type == RecordType.INCOME) {
                    root.context.getString(R.string.income)
                } else {
                    root.context.getString(R.string.expense)
                }

                // Set amount color based on type
                tvAmount.setTextColor(
                    if (record.type == RecordType.INCOME) {
                        root.context.getColor(android.R.color.holo_green_dark)
                    } else {
                        root.context.getColor(android.R.color.holo_red_dark)
                    }
                )

                // Set type chip background color
                tvType.setBackgroundColor(
                    if (record.type == RecordType.INCOME) {
                        root.context.getColor(android.R.color.holo_green_dark)
                    } else {
                        root.context.getColor(android.R.color.holo_red_dark)
                    }
                )

                // Set favorite icon
                ivFavorite.setImageResource(
                    if (record.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                )

                // Click listeners
                btnEdit.setOnClickListener { onEditClick(record) }
                btnDelete.setOnClickListener { onDeleteClick(record) }
                ivFavorite.setOnClickListener {
                    onFavoriteClick(record, !record.isFavorite)
                }
            }
        }
    }

    private class RecordDiffCallback : DiffUtil.ItemCallback<Record>() {
        override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
            return oldItem == newItem
        }
    }
}

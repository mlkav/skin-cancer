package com.dicoding.asclepius.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.PredictionEntity
import com.dicoding.asclepius.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<PredictionEntity, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }

    class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: PredictionEntity) {
            binding.ivItemImage.setImageURI(history.imageUri.toUri())
            binding.tvItemResult.text = history.result
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            binding.tvItemTimestamp.text = sdf.format(Date(history.timestamp))
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PredictionEntity>() {
            override fun areItemsTheSame(oldItem: PredictionEntity, newItem: PredictionEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: PredictionEntity, newItem: PredictionEntity): Boolean {
                return oldItem == newItem
            }
        }
    }
}

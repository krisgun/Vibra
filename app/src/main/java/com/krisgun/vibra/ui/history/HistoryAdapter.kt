package com.krisgun.vibra.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.krisgun.vibra.R
import com.krisgun.vibra.data.Measurement
import com.krisgun.vibra.databinding.ListItemHistoryBinding

class HistoryAdapter(
        private val measurements: List<Measurement>,
        private val listener: RecyclerViewClickListener) :
    RecyclerView.Adapter<HistoryAdapter.MeasurementsViewHolder>() {

    override fun getItemCount(): Int {
        return measurements.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MeasurementsViewHolder(
                DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.list_item_history,
                        parent,
                        false
                )
        )

    override fun onBindViewHolder(holder: MeasurementsViewHolder, position: Int) {
        holder.listItemHistoryBinding.measurement = measurements[position]
        holder.listItemHistoryBinding.constraintLayout.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.listItemHistoryBinding.constraintLayout, measurements[position])
        }

        holder.listItemHistoryBinding.listItemMore.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.listItemHistoryBinding.listItemMore, measurements[position])
        }
    }

    inner class MeasurementsViewHolder(val listItemHistoryBinding: ListItemHistoryBinding) :
        RecyclerView.ViewHolder(listItemHistoryBinding.root)
}
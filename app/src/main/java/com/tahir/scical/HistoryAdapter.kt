package com.tahir.scical

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val historyList: MutableList<HistoryItem>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFormula: TextView = view.findViewById(R.id.tvHistoryFormula)
        val tvResult: TextView = view.findViewById(R.id.tvHistoryResult)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.tvFormula.text = item.formula
        holder.tvResult.text = item.result
    }

    override fun getItemCount() = historyList.size

    fun removeItem(position: Int) {
        historyList.removeAt(position)
        notifyItemRemoved(position)
    }
}

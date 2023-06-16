package com.project.capstoneapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.capstoneapp.databinding.ItemDateListBinding
import com.project.capstoneapp.data.model.DateHistory

class DateHistoryAdapter(
    private val dateList: ArrayList<DateHistory>
) : RecyclerView.Adapter<DateHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = ItemDateListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(dateList[position])
        holder.setSelectedDate(dateList[position].isSelected)
        holder.itemView.setOnClickListener {
            for (i in 0 until dateList.size) {
                dateList[i].isSelected = false
            }

            dateList[position].isSelected = true

            for (i in 0 until dateList.size) {
                Log.d("TAG", "onBindViewHolder: ${dateList[i].isSelected} HAHA ke $i")
            }
            holder.setSelectedDate(dateList[position].isSelected)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = dateList.size

    class ViewHolder(private var itemBinding: ItemDateListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun setData(dateHistory: DateHistory) {
            itemBinding.apply {
                tvDay.text = dateHistory.day
                tvDate.text = dateHistory.date.toString()
            }
        }

        fun setSelectedDate(isSelected: Boolean) {
            if (isSelected) {
                itemBinding.layoutBackgroundSelected.visibility = View.VISIBLE
            } else {
                itemBinding.layoutBackgroundSelected.visibility = View.GONE
            }
        }
    }
}
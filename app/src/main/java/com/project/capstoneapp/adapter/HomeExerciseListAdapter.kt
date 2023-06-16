package com.project.capstoneapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.mystoryapp.utils.formatTimeFromMinutes
import com.project.capstoneapp.data.remote.response.HistoryResponse
import com.project.capstoneapp.databinding.ItemHomeExerciseListBinding
import java.text.DecimalFormat

class HomeExerciseListAdapter :
    RecyclerView.Adapter<HomeExerciseListAdapter.HomeExerciseListViewHolder>() {
    private val exerciseList = ArrayList<HistoryResponse>()

    private var onItemClickCallback: OnItemClickCallback? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HomeExerciseListViewHolder {
        val binding = ItemHomeExerciseListBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return HomeExerciseListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeExerciseListViewHolder, position: Int) {
        val item = exerciseList[position]
        holder.setItemData(item)
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(exerciseList[holder.adapterPosition])
        }
    }

    override fun getItemCount() = exerciseList.size

    class HomeExerciseListViewHolder(private var itemBinding: ItemHomeExerciseListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun setItemData(historyResponse: HistoryResponse) {
            itemBinding.tvExerciseName.text = historyResponse.name
            itemBinding.tvExerciseDuration.text =
                formatTimeFromMinutes(historyResponse.durasiMenit!!.toInt())
            itemBinding.tvExerciseTime.text = historyResponse.createdAt
            itemBinding.tvExerciseCalorie.text =
                StringBuilder(DecimalFormat("0.0").format(historyResponse.calorie))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setExerciseList(exerciseList: ArrayList<HistoryResponse>) {
        this.exerciseList.clear()
        this.exerciseList.addAll(exerciseList)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: HistoryResponse)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}
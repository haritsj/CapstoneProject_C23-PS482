package com.project.capstoneapp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.capstoneapp.data.remote.response.HistoryResponse
import com.project.capstoneapp.databinding.ItemHomeFoodListBinding
import java.text.DecimalFormat

class HomeFoodListAdapter: RecyclerView.Adapter<HomeFoodListAdapter.HomeFoodListViewHolder>() {
    private val foodList = ArrayList<HistoryResponse>()

    private var onItemClickCallback: OnItemClickCallback? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): HomeFoodListViewHolder {
        val binding = ItemHomeFoodListBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return HomeFoodListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeFoodListViewHolder, position: Int) {
        val item = foodList[position]
        holder.setItemData(item)
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(foodList[holder.adapterPosition])
        }
    }

    override fun getItemCount() = foodList.size

    class HomeFoodListViewHolder(private var itemBinding: ItemHomeFoodListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun setItemData(historyResponse: HistoryResponse) {
            itemBinding.tvFoodName.text = historyResponse.name
            itemBinding.tvFoodMenu.text = StringBuilder("${historyResponse.menu}")
            itemBinding.tvFoodTime.text = historyResponse.createdAt
            itemBinding.tvFoodQuantity.text = historyResponse.quantity.toString()
            itemBinding.tvFoodCalorie.text =
                StringBuilder(DecimalFormat("0.0").format(historyResponse.calorie))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFoodList(foodList: ArrayList<HistoryResponse>) {
        this.foodList.clear()
        this.foodList.addAll(foodList)
        notifyDataSetChanged()
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: HistoryResponse)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}